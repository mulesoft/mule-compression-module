/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.api.strategy.zip;

import org.mule.extension.compression.api.strategy.ExtractorStrategy;
import org.mule.extension.compression.internal.error.exception.DecompressionException;
import org.mule.extension.compression.internal.error.exception.InvalidArchiveException;
import org.mule.extension.compression.internal.zip.ZipEntryExtractor;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.exception.ModuleException;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

/**
 * A Zip extractor.
 *
 * @since 2.0
 */
@DisplayName("Zip")
@Alias("zip-extractor")
public class ZipExtractorStrategy implements ExtractorStrategy {

  private static final ZipEntryExtractor entryExtractor = new ZipEntryExtractor();

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, InputStream> extract(TypedValue<InputStream> archive) {
    InputStream content = archive.getValue();
    Map<String, InputStream> entries = new HashMap<>();
    try (ZipInputStream zip = new ZipInputStream(content)) {
      ZipEntry entry = zip.getNextEntry();
      if (entry == null) {
        throw new InvalidArchiveException();
      }
      while (entry != null) {
        entries.put(entry.getName(), entryExtractor.extractEntry(zip));
        entry = zip.getNextEntry();
      }
    } catch (ModuleException e) {
      throw e;
    } catch (ZipException e) {
      throw new InvalidArchiveException(e);
    } catch (Exception e) {
      throw new DecompressionException(e);
    }
    return entries;
  }
}
