/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.api.strategy.zip;

import org.mule.extension.compression.api.strategy.CompressorStrategy;
import org.mule.extension.compression.internal.error.exception.CompressionException;
import org.mule.extension.compression.internal.zip.ZipArchiveInputStream;
import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.operation.Result;

import java.io.InputStream;

import static java.util.Collections.singletonMap;

/**
 *
 * @since 2.0
 */
@DisplayName("Zip")
@Alias("zip-compressor")
public class ZipCompressorStrategy implements CompressorStrategy {

  private static final MediaType ZIP_MEDIA_TYPE = MediaType.create("application", "zip");

  /**
   * {@inheritDoc}
   */
  @Override
  public Result<InputStream, Void> compress(TypedValue<InputStream> data) throws CompressionException {
    return Result.<InputStream, Void>builder()
        .output(new ZipArchiveInputStream(singletonMap("data", data.getValue())))
        .mediaType(ZIP_MEDIA_TYPE)
        .build();
  }
}
