/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.internal.zip;

import static org.mule.extension.compression.api.CompressionError.INVALID_ZIP;
import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;
import org.mule.extension.compression.internal.exception.DecompressionException;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.extension.api.runtime.operation.Result;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

/**
 * Operations to handle ZIP contents
 *
 * @since 1.0
 */
public class ZipOperations {

  /**
   * Compresses the given {@code content} using the ZIP format.
   * <p>
   * Actual compression will occur in a lazy manner. This operation returns a stream and actual compression will happen
   * as the stream is consumed. Keep in mind that any errors that may rise during compression will not occur in this operation,
   * but in those which are consuming the stream returned here.
   *
   * @param content the content to be compressed
   * @return a stream
   */
  @MediaType(value = ANY, strict = false)
  @Summary("Compresses the given content using the ZIP format")
  public Result<InputStream, Void> zip(@Content TypedValue<InputStream> content) {
    return Result
        .<InputStream, Void>builder()
        .output(new GZIPCompressorInputStream(content.getValue()))
        .mediaType(content.getDataType().getMediaType())
        .build();
  }

  /**
   * Decompresses the given {@code content} which is assumed to be in ZIP format.
   * <p>
   * Actual compression will occur in a lazy manner. This operation returns a stream and actual decompression will happen
   * as the stream is consumed. Keep in mind that not all  errors that may rise during compression will occur in this operation.
   * Some might actual appear in the components that consume this stream
   *
   * @param content the content to be decompressed
   * @return a stream
   */
  @MediaType(value = ANY, strict = false)
  @Throws(UnzipErrorProvider.class)
  @Summary("Decompresses the given content which is assumed to be in ZIP format")
  public Result<InputStream, Void> unzip(@Content TypedValue<InputStream> content) {
    try {
      return Result
          .<InputStream, Void>builder()
          .output(new GZIPInputStream(content.getValue()))
          .mediaType(content.getDataType().getMediaType())
          .build();
    } catch (ZipException e) {
      throw new ModuleException("The provided ZIP content is invalid: " + e.getMessage(), INVALID_ZIP, e);
    } catch (IOException e) {
      throw new DecompressionException(e);
    }
  }
}
