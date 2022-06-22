/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.api.strategy.zip;

import static java.util.Collections.singletonMap;
import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.mule.extension.compression.api.strategy.CompressorStrategy;
import org.mule.extension.compression.internal.CompressionManager;
import org.mule.extension.compression.internal.error.exception.CompressionException;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.operation.Result;

import java.io.InputStream;

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

  @Parameter
  @DisplayName("Force ZIP64")
  @Optional(defaultValue = "false")
  @Expression(NOT_SUPPORTED)
  boolean forceZip64;

  /**
   * {@inheritDoc}
   */
  @Override
  public Result<InputStream, Void> compress(TypedValue<InputStream> data) throws CompressionException {
    return compressionManager.asyncArchive(singletonMap("data", data), forceZip64);
  }

  public boolean isForceZip64() {
    return forceZip64;
  }

  public void setForceZip64(boolean forceZip64) {
    this.forceZip64 = forceZip64;
  }
}
