/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.api.strategy.zip;

import static java.util.Collections.singletonMap;
import static org.mule.extension.compression.internal.CompressionExtension.ZIP_MEDIA_TYPE;
import static org.mule.extension.compression.internal.zip.ZipUtils.archive;
import org.mule.extension.compression.api.strategy.CompressorStrategy;
import org.mule.extension.compression.internal.CompressionManager;
import org.mule.extension.compression.internal.error.exception.CompressionException;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.transformation.TransformationService;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.operation.Result;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.inject.Inject;

/**
 * A Zip compressor.
 *
 * @since 2.0
 */
@DisplayName("Zip")
@Alias("zip-compressor")
public class ZipCompressorStrategy implements CompressorStrategy {

  @Inject
  private CompressionManager compressionManager;

  @Inject
  private TransformationService transformationService;

  /**
   * {@inheritDoc}
   */
  @Override
  public Result<InputStream, Void> compress(TypedValue<InputStream> data) throws CompressionException {
    try {
      PipedInputStream stream = new PipedInputStream();
      PipedOutputStream out = new PipedOutputStream(stream);

      compressionManager.getCompressionScheduler().submit(() -> archive(singletonMap("data", data), out, transformationService));

      return Result.<InputStream, Void>builder()
          .output(stream)
          .mediaType(ZIP_MEDIA_TYPE)
          .build();
    } catch (CompressionException e) {
      throw e;
    } catch (Throwable t) {
      throw new CompressionException(t);
    }
  }
}
