/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mule.extension.compression.api.strategy.gzip.GzipCompressorStrategy;
import org.mule.extension.compression.api.strategy.gzip.GzipDecompressorStrategy;
import org.mule.extension.compression.internal.error.exception.InvalidArchiveException;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.core.api.util.IOUtils;
import org.mule.runtime.extension.api.runtime.operation.Result;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static java.lang.Thread.currentThread;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mule.extension.compression.CompressionModuleTestUtils.*;
import static org.mule.runtime.api.metadata.DataType.TEXT_STRING;
import static org.mule.runtime.core.api.util.IOUtils.toByteArray;

public class GzipTestCase {


  private static final String GZIP_TEST_FILE_NAME = "file.txt.gz";

  @Rule
  public ExpectedException expected = ExpectedException.none();

  private GzipCompressorStrategy compressor = new GzipCompressorStrategy();
  private GzipDecompressorStrategy decompressor = new GzipDecompressorStrategy();

  @Test
  public void compress() {
    TypedValue<InputStream> testInput = new TypedValue<>(new ByteArrayInputStream(TEST_DATA.getBytes()), TEXT_STRING);
    Result<InputStream, Void> compress = compressor.compress(testInput);
    byte[] gzipBytes = toByteArray(compress.getOutput());

    assertThat(gzipBytes.length, lessThan(DATA_SIZE));
  }

  @Test
  public void decompress() {
    InputStream testInput = currentThread().getContextClassLoader().getResourceAsStream(GZIP_TEST_FILE_NAME);
    InputStream decompress = decompressor.decompress(new TypedValue<>(testInput, TEXT_STRING));
    byte[] resultBytes = toByteArray(decompress);
    byte[] expectedBytes = toByteArray(currentThread().getContextClassLoader().getResourceAsStream(GZIP_TEST_FILE_NAME));

    assertThat(resultBytes.length, greaterThan(expectedBytes.length));
    assertThat(new String(resultBytes), equalToIgnoringWhiteSpace(FILE_TXT_DATA));
  }

  @Test
  public void doRoundTrip() {
    TypedValue<InputStream> testInput = new TypedValue<>(new ByteArrayInputStream(TEST_DATA.getBytes()), TEXT_STRING);
    Result<InputStream, Void> compress = compressor.compress(testInput);
    InputStream decompress = decompressor.decompress(new TypedValue<>(compress.getOutput(), TEXT_STRING));
    assertThat(IOUtils.toString(decompress), equalTo(TEST_DATA));
  }

  @Test
  public void invalidFile() {
    expected.expect(InvalidArchiveException.class);
    expected.expectMessage("Error decompressing: The provided archive is not valid - Not in GZIP format");

    InputStream fileTxt = currentThread().getContextClassLoader().getResourceAsStream(FILE_TXT_NAME);
    TypedValue<InputStream> txtInput = new TypedValue<>(fileTxt, TEXT_STRING);
    decompressor.decompress(txtInput);
  }
}
