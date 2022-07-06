/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression;

import static org.mule.runtime.api.metadata.DataType.INPUT_STREAM;
import static java.lang.Thread.*;

import org.mule.extension.compression.api.strategy.zip.ZipArchiverStrategy;
import org.mule.extension.compression.api.strategy.zip.ZipCompressorStrategy;
import org.mule.extension.compression.api.strategy.zip.ZipDecompressorStrategy;
import org.mule.extension.compression.api.strategy.zip.ZipExtractorStrategy;
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.runtime.operation.Result;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import com.google.common.collect.ImmutableMap;

public class Zip64ModeTests extends FunctionalTestCase {

  @Rule
  public ExpectedException expected = ExpectedException.none();

  private ZipArchiverStrategy archiver = new ZipArchiverStrategy();
  private ZipExtractorStrategy extractor = new ZipExtractorStrategy();
  private ZipCompressorStrategy compressor = new ZipCompressorStrategy();
  private ZipDecompressorStrategy decompressor = new ZipDecompressorStrategy();

  @Override
  protected String[] getConfigFiles() {
    return new String[] {};
  }

  @Override
  protected void doSetUp() throws Exception {
    muleContext.getInjector().inject(archiver);
    muleContext.getInjector().inject(extractor);
    muleContext.getInjector().inject(compressor);
    muleContext.getInjector().inject(decompressor);
  }

  @Before
  public void setup() {}

  @Test
  public void archiveInputStreamGreaterThan4GBNotForceZIP64() throws Exception {

    archiver.setForceZip64(false);
    Map<String, TypedValue<InputStream>> testEntries = getTestEntries();
    Result<InputStream, Void> compress = archiver.archive(testEntries);

    InputStream output = compress.getOutput();
    consumeOutput(output);
    //TODO - Fix error handling in CompressionManager and add the following lines:
    // ----
    // expected.expect(CompressionException.class);
    // expected.expectMessage("Unexpected error occur while trying to compress: data1's size exceeds the limit of 4GByte.");
  }

  /**
   * This method receive an inputStream, and consume all bytes without any action,
   * is necessary to get an error for test
   */
  private void consumeOutput(InputStream is) throws IOException {
    int chunk = 0;
    byte[] buffer = new byte[1024];
    while ((chunk = is.read(buffer)) != -1);
  }

  private Map<String, TypedValue<InputStream>> getTestEntries() {
    CustomSizeInputStream inputStream = new CustomSizeInputStream(4295709120L);
    TypedValue<InputStream> testInput = new TypedValue<>(inputStream, INPUT_STREAM);

    return ImmutableMap.<String, TypedValue<InputStream>>builder()
        .put("data1", testInput)
        .build();
  }
}
