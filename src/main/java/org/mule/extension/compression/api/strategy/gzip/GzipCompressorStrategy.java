/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.api.strategy.gzip;

import org.mule.extension.compression.api.strategy.CompressorStrategy;
import org.mule.extension.compression.internal.error.exception.CompressionException;
import org.mule.extension.compression.internal.gzip.GzipCompressorInputStream;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.operation.Result;

import java.io.InputStream;

/**
 * A Gzip compressor.
 *
 * @since 2.0
 */
@DisplayName("Gzip")
@Alias("gzip-compressor")
public class GzipCompressorStrategy implements CompressorStrategy {

  /**
   * {@inheritDoc}
   */
  @Override
  public Result<InputStream, Void> compress(TypedValue<InputStream> data) {
    try {
      return Result.<InputStream, Void>builder()
          .output(new GzipCompressorInputStream(data.getValue()))
          .mediaType(data.getDataType().getMediaType())
          .build();
    } catch (Exception e) {
      throw new CompressionException(e);
    }
  }
}
