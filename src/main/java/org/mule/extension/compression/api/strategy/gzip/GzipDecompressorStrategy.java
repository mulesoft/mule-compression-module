/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.api.strategy.gzip;

import org.mule.extension.compression.api.strategy.DecompressorStrategy;
import org.mule.extension.compression.internal.error.exception.DecompressionException;
import org.mule.extension.compression.internal.error.exception.InvalidArchiveException;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

/**
 *
 * @since 2.0
 */
@DisplayName("Gzip")
@Alias("gzip-decompressor")
public class GzipDecompressorStrategy implements DecompressorStrategy {

  /**
   * {@inheritDoc}
   */
  @Override
  public InputStream decompress(TypedValue<InputStream> compressed) {
    try {
      return new GZIPInputStream(compressed.getValue());
    } catch (ZipException e) {
      throw new InvalidArchiveException(e);
    } catch (IOException e) {
      throw new DecompressionException(e);
    }
  }
}
