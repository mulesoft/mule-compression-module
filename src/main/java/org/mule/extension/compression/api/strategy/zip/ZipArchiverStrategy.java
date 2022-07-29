/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.api.strategy.zip;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

import org.mule.extension.compression.api.strategy.ArchiverStrategy;
import org.mule.extension.compression.internal.CompressionManager;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.operation.Result;

import java.io.InputStream;
import java.util.Map;

import javax.inject.Inject;

/**
 * Zip format archiver
 *
 * @since 2.0
 */
@DisplayName("Zip")
@Alias("zip-archiver")
public class ZipArchiverStrategy implements ArchiverStrategy {

  @Inject
  private CompressionManager compressionManager;

  /**
   * Enables you to archive files and byte arrays greater than 4 GB.
   */
  @Parameter
  @DisplayName("Force ZIP64")
  @Optional(defaultValue = "false")
  @Expression(NOT_SUPPORTED)
  boolean forceZip64;

  /**
   * If this flag is enabled, the module will ignore exceptions produced during the compression
   */
  @Parameter
  @DisplayName("Ignore errors when archiving")
  @Optional(defaultValue = "true")
  @Expression(NOT_SUPPORTED)
  boolean ignoreErrorsWhenCompressing;

  /**
   * {@inheritDoc}
   */
  @Override
  public Result<InputStream, Void> archive(Map<String, TypedValue<InputStream>> entries) {
    return compressionManager.asyncArchive(entries, forceZip64, ignoreErrorsWhenCompressing);
  }

  public boolean isForceZip64() {
    return forceZip64;
  }

  public void setForceZip64(boolean forceZip64) {
    this.forceZip64 = forceZip64;
  }

  public boolean isIgnoreErrorsWhenCompressing() {
    return ignoreErrorsWhenCompressing;
  }

  public void setIgnoreErrorsWhenCompressing(boolean ignoreErrorsWhenCompressing) {
    this.ignoreErrorsWhenCompressing = ignoreErrorsWhenCompressing;
  }
}
