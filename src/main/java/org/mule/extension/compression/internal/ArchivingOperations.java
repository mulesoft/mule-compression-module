/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.internal;

import org.mule.extension.compression.api.strategy.ArchiverStrategy;
import org.mule.extension.compression.api.strategy.ExtractorStrategy;
import org.mule.extension.compression.internal.error.exception.CompressionException;
import org.mule.extension.compression.internal.error.exception.NullArchiveException;
import org.mule.extension.compression.internal.error.providers.ArchiveErrorProvider;
import org.mule.extension.compression.internal.error.providers.ExtractErrorProvider;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.streaming.CursorProvider;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.operation.Result;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;
import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

/**
 * Archive and Extract operations
 *
 * @since 2.0
 */
public class ArchivingOperations {

  /**
   * Compresses all the given entries into a new file in the configured format.
   * <p>
   * Each entry passed to this operation will be placed inside the compressed archive with the name provided by the user
   * in the DataWeave script. e.g.
   *
   * For this expression:
   *
   * {
   *   file: vars.aTxtContent,
   *   'dir/resume.pdf': vars.pdf
   * }
   *
   * The resultant archive will contain 2 entries one named "file" at root level and another one called "resume.pdf" inside a
   * directory called "dir".
   *
   *  +- Archive
   *  |  \- file1
   *  |  \+ dir1
   *     |  \- file2
   *
   * As you can see in the example above, the slash "/" in the name of an entry indicates directory separation, so all names will
   * be introspected to create dirs inside the archive.
   */
  @MediaType(value = ANY, strict = false)
  @Summary("Compresses a set of entries into a new file in the specified archive format")
  @Throws(ArchiveErrorProvider.class)
  public Result<InputStream, Void> archive(@Content Map<String, TypedValue<InputStream>> entries,
                                           @ParameterDsl(
                                               allowReferences = false) @Expression(NOT_SUPPORTED) ArchiverStrategy archiver) {
    if (entries == null) {
      throw new CompressionException("the entries parameter is null");
    }
    return archiver.archive(entries);
  }

  /**
   * Decompresses a given content that represent an archive in a compression format.
   */
  @MediaType(value = ANY, strict = false)
  @Throws(ExtractErrorProvider.class)
  @Summary("Decompresses a compressed content in the configured returning a list with all the entries inside it uncompressed")
  public Map<String, InputStream> extract(@Content TypedValue<InputStream> compressed,
                                          @ParameterDsl(
                                              allowReferences = false) @Expression(NOT_SUPPORTED) ExtractorStrategy extractor) {
    if (compressed.getValue() == null) {
      throw new NullArchiveException();
    }
    return extractor.extract(compressed);
  }
}
