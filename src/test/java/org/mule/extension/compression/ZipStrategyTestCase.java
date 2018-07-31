/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression;

import com.google.common.collect.ImmutableMap;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mule.extension.compression.internal.error.exception.InvalidArchiveException;
import org.mule.extension.compression.internal.error.exception.TooManyEntriesException;
import org.mule.extension.compression.api.strategy.zip.ZipArchiverStrategy;
import org.mule.extension.compression.api.strategy.zip.ZipCompressorStrategy;
import org.mule.extension.compression.api.strategy.zip.ZipDecompressorStrategy;
import org.mule.extension.compression.api.strategy.zip.ZipExtractorStrategy;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.core.api.util.IOUtils;
import org.mule.runtime.extension.api.runtime.operation.Result;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import static java.lang.Thread.currentThread;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mule.extension.compression.CompressionModuleTestUtils.*;
import static org.mule.runtime.api.metadata.DataType.INPUT_STREAM;
import static org.mule.runtime.api.metadata.DataType.TEXT_STRING;
import static org.mule.runtime.core.api.util.IOUtils.toByteArray;

public class ZipStrategyTestCase {

  private static final String ZIP_TEST_SINGLE_ENTRY_ARCHIVE_NAME = "file.txt.zip";
  private static final String ZIP_TEST_ARCHIVE_NAME = "archive.zip";

  private static final String FILE_CONTENT_INSIDE_DIR = "This is the content of a file inside a directory in a zip";

  @Rule
  public ExpectedException expected = ExpectedException.none();

  private ZipCompressorStrategy compressor = new ZipCompressorStrategy();
  private ZipArchiverStrategy archiver = new ZipArchiverStrategy();
  private ZipDecompressorStrategy decompressor = new ZipDecompressorStrategy();
  private ZipExtractorStrategy extractor = new ZipExtractorStrategy();

  @Test
  public void compress() {
    TypedValue<InputStream> testInput = new TypedValue<>(new ByteArrayInputStream(TEST_DATA.getBytes()), TEXT_STRING);
    Result<InputStream, Void> compress = compressor.compress(testInput);
    byte[] gzipBytes = toByteArray(compress.getOutput());

    assertThat(gzipBytes.length, lessThan(DATA_SIZE));
  }

  @Test
  public void archive() {
    Map<String, InputStream> testEntries = getTestEntries();
    Result<InputStream, Void> compress = archiver.archive(testEntries);
    byte[] zipBytes = toByteArray(compress.getOutput());

    assertThat(zipBytes.length, lessThan(DATA_SIZE * testEntries.size()));
  }

  @Test
  public void decompress() {
    InputStream testInput = currentThread().getContextClassLoader().getResourceAsStream(ZIP_TEST_SINGLE_ENTRY_ARCHIVE_NAME);
    InputStream decompress = decompressor.decompress(new TypedValue<>(testInput, TEXT_STRING));
    byte[] resultBytes = toByteArray(decompress);
    InputStream expected = currentThread().getContextClassLoader().getResourceAsStream(ZIP_TEST_SINGLE_ENTRY_ARCHIVE_NAME);
    byte[] expectedBytes = toByteArray(expected);

    assertThat(resultBytes.length, greaterThan(expectedBytes.length));
    assertThat(new String(resultBytes), equalTo(FILE_TXT_DATA));
  }

  @Test
  public void decompressTooManyEntries() {
    expected.expect(TooManyEntriesException.class);
    expected.expectMessage("Expected a single entry archive but got multiple");

    InputStream testInput = currentThread().getContextClassLoader().getResourceAsStream(ZIP_TEST_ARCHIVE_NAME);
    decompressor.decompress(new TypedValue<>(testInput, TEXT_STRING));
  }

  @Test
  public void extract() {
    InputStream testInput = currentThread().getContextClassLoader().getResourceAsStream(ZIP_TEST_ARCHIVE_NAME);
    Map<String, InputStream> extracted = extractor.extract(new TypedValue<>(testInput, TEXT_STRING));

    String fileInDirName = "dir/in-dir.txt";
    assertThat(extracted, IsMapContaining.hasKey(fileInDirName));
    assertThat(extracted, IsMapContaining.hasKey(FILE_TXT_NAME));
    assertThat(IOUtils.toString(extracted.get(fileInDirName)), is(FILE_CONTENT_INSIDE_DIR));
    assertThat(IOUtils.toString(extracted.get(FILE_TXT_NAME)), is(FILE_TXT_DATA));
  }

  @Test
  public void doCompressionRoundTrip() {
    Result<InputStream, Void> compressed = compressor.compress(asTextTypedValue(new ByteArrayInputStream(TEST_DATA.getBytes())));
    InputStream decompressed = decompressor.decompress(asTextTypedValue(compressed.getOutput()));

    assertThat(IOUtils.toString(decompressed), is(TEST_DATA));
  }

  @Test
  public void doArchiveRoundTrip() {
    // TODO
    //    Result<InputStream, Void> compressed = compressor.compress(asTextTypedValue(new ByteArrayInputStream(TEST_DATA.getBytes())));
    //    Result<InputStream, EntryAttributes> decompressed = decompressor.decompress(asTextTypedValue(compressed.getOutput()));

    //    assertThat(IOUtils.toString(decompressed.getOutput()), is(TEST_DATA));
  }

  @Test
  public void invalidFile() {
    expected.expect(InvalidArchiveException.class);
    expected.expectMessage("Error decompressing: The provided archive is not valid");

    decompressor
        .decompress(new TypedValue<>(new ByteArrayInputStream("INVALID CONTENT - NOT A ZIP FILE".getBytes()), INPUT_STREAM));
  }

  private Map<String, InputStream> getTestEntries() {
    return ImmutableMap.<String, InputStream>builder()
        .put("data1", new ByteArrayInputStream(TEST_DATA.getBytes()))
        .put("data2", new ByteArrayInputStream(TEST_DATA.getBytes()))
        .put("data3", new ByteArrayInputStream(TEST_DATA.getBytes()))
        .build();
  }
}
