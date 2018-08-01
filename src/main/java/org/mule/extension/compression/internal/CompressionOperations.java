/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.internal;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;
import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

import org.mule.extension.compression.api.strategy.DecompressorStrategy;
import org.mule.extension.compression.internal.error.exception.CompressionException;
import org.mule.extension.compression.internal.error.exception.NullArchiveException;
import org.mule.extension.compression.internal.error.providers.CompressErrorProvider;
import org.mule.extension.compression.api.strategy.CompressorStrategy;
import org.mule.extension.compression.internal.error.providers.DecompressErrorProvider;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.operation.Result;

import java.io.InputStream;

/**
 * Compression and Decompression operations
 *
 * @since 1.0
 */
public class CompressionOperations {

  /**
   * Compresses a given content using the configured format, ZIP or GZIP.
   *
   * If a problem occur while compressing the content a COULD_NOT_COMPRESS error will be thrown.
   */
  @MediaType(value = ANY, strict = false)
  @Summary("Compresses a given content using an specific format")
  @Throws(CompressErrorProvider.class)
  public Result<InputStream, Void> compress(@Content TypedValue<InputStream> content,
                                            @Expression(NOT_SUPPORTED) CompressorStrategy compressor) {
    if (content.getValue() == null) {
      throw new CompressionException("cannot compress null content");
    }
    return compressor.compress(content);
  }


  /**
   * Decompresses a given single entry compressed content which is assumed to be in an specific format (ZIP or GZIP).
   *
   * If the given content is not in the configured format an INVALID_ARCHIVE error will be thrown.
   *
   * Compressed archives can have multiple entries, this operation can handle only single entry archives because if the archive
   * would contain more than one the operation wouldn't know which should return, if the content has more than one entry an
   * TOO_MANY_ENTRIES error will be thrown. For multiple entry archives use the `extract` operation of this module, which will
   * let you choose only formats that accept multiple entries.
   */
  @MediaType(value = ANY, strict = false)
  @Throws(DecompressErrorProvider.class)
  @Summary("Decompresses a single entry compressed content")
  public InputStream decompress(@Content TypedValue<InputStream> compressed,
                                @Expression(NOT_SUPPORTED) DecompressorStrategy decompressor) {
    if (compressed.getValue() == null) {
      throw new NullArchiveException();
    }
    return decompressor.decompress(compressed);
  }
}
