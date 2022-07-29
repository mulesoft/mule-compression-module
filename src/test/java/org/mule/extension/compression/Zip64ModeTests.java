/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression;

import static org.mule.runtime.api.metadata.DataType.INPUT_STREAM;

import org.mule.extension.compression.api.strategy.zip.ZipArchiverStrategy;
import org.mule.extension.compression.api.strategy.zip.ZipExtractorStrategy;
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.runtime.operation.Result;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import com.google.common.collect.ImmutableMap;

public class Zip64ModeTests extends FunctionalTestCase {

  private static final long AMOUNT_OF_BYTES_GREATER_THAN_4GB = 4295709120L;

  @Rule
  public ExpectedException expected = ExpectedException.none();

  private ZipArchiverStrategy archiver = new ZipArchiverStrategy();
  private ZipExtractorStrategy extractor = new ZipExtractorStrategy();

  @Override
  protected String[] getConfigFiles() {
    return new String[] {};
  }

  @Override
  protected void doSetUp() throws Exception {
    muleContext.getInjector().inject(archiver);
    muleContext.getInjector().inject(extractor);
  }

  @Test
  public void archiveInputStreamGreaterThan4GBNotForceZIP64MayThrowException() throws IOException {

    expected.expect(IOException.class);
    expected.expectMessage("Unexpected error occur while trying to compress: data1's size exceeds the limit of 4GByte.");

    archiver.setIgnoreErrorsWhenArchiving(false);
    archiver.setForceZip64(false);
    Map<String, TypedValue<InputStream>> testEntries = getTestEntries();
    Result<InputStream, Void> compress = archiver.archive(testEntries);

    InputStream output = compress.getOutput();
    while (output.read() != -1);
    output.read();
  }

  @Test
  public void archiveInputStreamGreaterThan4GBNotForceZIP64MayNotThrowException() throws IOException {

    archiver.setIgnoreErrorsWhenArchiving(true);
    archiver.setForceZip64(false);
    Map<String, TypedValue<InputStream>> testEntries = getTestEntries();
    Result<InputStream, Void> compress = archiver.archive(testEntries);

    InputStream output = compress.getOutput();
    while (output.read() != -1);
  }

  @Test
  public void archiveInputStreamGreaterThan4GBForceZIP64() throws IOException {

    archiver.setForceZip64(true);
    Map<String, TypedValue<InputStream>> testEntries = getTestEntries();
    Result<InputStream, Void> compress = archiver.archive(testEntries);

    InputStream output = compress.getOutput();
    while (output.read() != -1);
  }

  private Map<String, TypedValue<InputStream>> getTestEntries() {
    return ImmutableMap.<String, TypedValue<InputStream>>builder()
        .put("data1", getEntry())
        .build();
  }

  private TypedValue<InputStream> getEntry() {
    CustomSizeInputStream inputStream = new CustomSizeInputStream(AMOUNT_OF_BYTES_GREATER_THAN_4GB);
    TypedValue<InputStream> testInput = new TypedValue<>(inputStream, INPUT_STREAM);
    return testInput;
  }
}
