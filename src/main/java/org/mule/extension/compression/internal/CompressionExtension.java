/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.internal;

import static org.mule.runtime.api.metadata.MediaType.create;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_11;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_17;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_8;

import org.mule.extension.compression.api.strategy.ArchiverStrategy;
import org.mule.extension.compression.api.strategy.CompressorStrategy;
import org.mule.extension.compression.api.strategy.DecompressorStrategy;
import org.mule.extension.compression.api.strategy.ExtractorStrategy;
import org.mule.extension.compression.api.strategy.gzip.GzipCompressorStrategy;
import org.mule.extension.compression.api.strategy.gzip.GzipDecompressorStrategy;
import org.mule.extension.compression.api.strategy.zip.ZipArchiverStrategy;
import org.mule.extension.compression.api.strategy.zip.ZipCompressorStrategy;
import org.mule.extension.compression.api.strategy.zip.ZipDecompressorStrategy;
import org.mule.extension.compression.api.strategy.zip.ZipExtractorStrategy;
import org.mule.extension.compression.internal.error.CompressionError;
import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.SubTypeMapping;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;
import org.mule.sdk.api.annotation.JavaVersionSupport;

/**
 * A module which provides functionality for compressing and decompressing data.
 *
 * @since 1.0
 */
@JavaVersionSupport({JAVA_8, JAVA_11, JAVA_17})
@SubTypeMapping(baseType = CompressorStrategy.class, subTypes = {GzipCompressorStrategy.class, ZipCompressorStrategy.class})
@SubTypeMapping(baseType = DecompressorStrategy.class, subTypes = {GzipDecompressorStrategy.class, ZipDecompressorStrategy.class})
@SubTypeMapping(baseType = ExtractorStrategy.class, subTypes = {ZipExtractorStrategy.class})
@SubTypeMapping(baseType = ArchiverStrategy.class, subTypes = {ZipArchiverStrategy.class})
@Extension(name = "Compression")
@Operations({CompressionOperations.class, ArchivingOperations.class})
@ErrorTypes(CompressionError.class)
public class CompressionExtension {

  /**
   * The primary compression format used in Windows
   */
  public static final MediaType ZIP_MEDIA_TYPE = create("application", "zip");

  /**
   * GNU Zip, the primary compression format used by Unix-like systems. The compression algorithm is DEFLATE.
   */
  public static final MediaType GZIP_MEDIA_TYPE = MediaType.create("application", "gzip");
}
