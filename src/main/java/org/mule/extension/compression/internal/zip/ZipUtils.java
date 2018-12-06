/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.internal.zip;

import static org.mule.runtime.api.metadata.DataType.INPUT_STREAM;
import org.mule.extension.compression.internal.error.exception.CompressionException;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.transformation.TransformationService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

/**
 * Utilities for handling zip files
 *
 * @since 2.0.2
 */
public class ZipUtils {

  /**
   * Creates an archive of the given entries
   *
   * @param entries the entries to archive
   * @param out the {@link OutputStream} in which the compressed content is going to be written
   * @param transformationService a {@link TransformationService}
   */
  public static void archive(Map<String, TypedValue<InputStream>> entries,
                             OutputStream out,
                             TransformationService transformationService) {
    try (ZipArchiveOutputStream zip = new ZipArchiveOutputStream(out)) {
      entries.forEach((name, content) -> addEntry(zip, name, content, transformationService));
    } catch (IOException e) {
      throw new CompressionException(e);
    }
  }

  private static void addEntry(ZipArchiveOutputStream zip,
                               String name,
                               TypedValue<InputStream> entryContent,
                               TransformationService transformationService) {
    try {
      ZipArchiveEntry newEntry = new ZipArchiveEntry(name);
      zip.putArchiveEntry(newEntry);

      byte[] buffer = new byte[1024];
      int length;
      InputStream content = getContent(name, entryContent, transformationService);

      while ((length = content.read(buffer)) >= 0) {
        zip.write(buffer, 0, length);
      }
      zip.closeArchiveEntry();
    } catch (Exception e) {
      throw new CompressionException(e);
    }
  }

  private static InputStream getContent(String name, TypedValue<?> entryContent, TransformationService transformationService) {
    try {
      Object value = entryContent.getValue();
      if (value instanceof InputStream) {
        return (InputStream) value;
      }
      return (InputStream) transformationService.transform(value, entryContent.getDataType(), INPUT_STREAM);
    } catch (Exception e) {
      throw new CompressionException("cannot archive entry [" + name + "], content cannot be transformed to InputStream");
    }
  }
}
