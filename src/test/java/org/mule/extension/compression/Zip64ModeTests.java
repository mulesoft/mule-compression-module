/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression;

import static org.mule.extension.compression.CompressionModuleTestUtils.TEST_DATA;
import static org.mule.runtime.api.metadata.DataType.INPUT_STREAM;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.mule.runtime.api.metadata.DataType.TEXT_STRING;

import org.mule.extension.compression.api.strategy.zip.ZipArchiverStrategy;
import org.mule.extension.compression.api.strategy.zip.ZipCompressorStrategy;
import org.mule.extension.compression.api.strategy.zip.ZipDecompressorStrategy;
import org.mule.extension.compression.api.strategy.zip.ZipExtractorStrategy;
import org.mule.extension.compression.internal.error.exception.CompressionException;
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.runtime.operation.Result;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import com.google.common.collect.ImmutableMap;

public class Zip64ModeTests extends FunctionalTestCase {

  private static final long AMOUNT_OF_BYTES_GREATER_THAN_4GB = 4295709120L;
  private static final int AMOUNT_OF_BYTES_IN_1GB = 1073741824;

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

  @Test
  public void archiveInputStreamGreaterThan4GBNotForceZIP64() throws Exception {

    archiver.setForceZip64(false);
    Map<String, TypedValue<InputStream>> testEntries = getTestEntries();
    Result<InputStream, Void> compress = archiver.archive(testEntries);

    InputStream output = compress.getOutput();

    consumeOutputAndReturnSize(output);
    //TODO - Fix error handling in CompressionManager (W-11390500) and add the following lines:

    //expected.expect(IOException.class);
    //  expected.expectMessage("Unexpected error occur while trying to compress: data1's size exceeds the limit of 4GByte.");
  }

  @Test
  public void archiveInputStreamGreaterThan4GBForceZIP64() throws IOException, InterruptedException {

    archiver.setForceZip64(true);
    Map<String, TypedValue<InputStream>> testEntries = getTestEntries();
    Result<InputStream, Void> compress = archiver.archive(testEntries);

    InputStream output = compress.getOutput();
    assertThat(consumeOutputAndReturnSize(output), lessThan(AMOUNT_OF_BYTES_IN_1GB));
  }

  /**
   * This method receive an inputStream, consume all bytes and calculate the total amount,
   * is necessary to get an error for test
   */
  private int consumeOutputAndReturnSize(InputStream is) throws IOException {

    int size = 0;
    while ((is.read()) != -1) {
      size++;
    }
    is.close();
    return size;
  }

  private Map<String, TypedValue<InputStream>> getTestEntries() {
    TypedValue<InputStream> testInput = new TypedValue<>(new ByteArrayInputStream(TEST_DATA.getBytes()), TEXT_STRING);
    return ImmutableMap.<String, TypedValue<InputStream>>builder()
        .put("data1", getEntry()).put("data2", testInput)
        .build();
  }

  private TypedValue<InputStream> getEntry() {
    CustomSizeInputStream inputStream = new CustomSizeInputStream(AMOUNT_OF_BYTES_GREATER_THAN_4GB);
    TypedValue<InputStream> testInput = new TypedValue<>(inputStream, INPUT_STREAM);
    return testInput;
  }


  private class ThreadReadInputStream implements Runnable {

    @Override
    public void run() {

    }
  }
}
