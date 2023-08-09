/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.api.strategy.zip;

import static org.apache.commons.compress.archivers.zip.ZipFile.closeQuietly;
import org.mule.extension.compression.api.strategy.DecompressorStrategy;
import org.mule.extension.compression.internal.CompressionManager;
import org.mule.extension.compression.internal.PostActionInputStreamWrapper;
import org.mule.extension.compression.internal.error.exception.DecompressionException;
import org.mule.extension.compression.internal.error.exception.InvalidArchiveException;
import org.mule.extension.compression.internal.error.exception.TooManyEntriesException;
import org.mule.extension.compression.internal.zip.TempZipFile;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.exception.ModuleException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipException;

import javax.inject.Inject;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import static org.slf4j.LoggerFactory.getLogger;
import org.slf4j.Logger;

/**
 * A Zip decompressor.
 *
 * @since 2.0
 */
@DisplayName("Zip")
@Alias("zip-decompressor")
public class ZipDecompressorStrategy implements DecompressorStrategy {

  @Inject
  private CompressionManager compressionManager;

  private static final Logger LOGGER = getLogger(ZipDecompressorStrategy.class);

  /**
   * {@inheritDoc}
   */
  @Override
  public InputStream decompress(TypedValue<InputStream> compressed) throws DecompressionException {
    TempZipFile zip = null;
    try {
      zip = compressionManager.toTempZip(compressed.getValue());
      Enumeration<ZipArchiveEntry> entries = zip.getEntries();
      if (!entries.hasMoreElements()) {
        throw new InvalidArchiveException("The provided archive has no entries");
      }

      final ZipArchiveEntry entry = entries.nextElement();
      if (entries.hasMoreElements()) {
        List<String> allEntries = new ArrayList<>();
        allEntries.add(entry.getName());
        do {
          allEntries.add(entries.nextElement().getName());
        } while (entries.hasMoreElements());

        throw new TooManyEntriesException(allEntries);
      }

      return new PostActionInputStreamWrapper(zip.getInputStream(entry), zip::closeSafely);
    } catch (ModuleException e) {
      closeQuietly(zip);
      throw e;
    } catch (Exception e) {
      closeQuietly(zip);
      if (e.getCause() instanceof ZipException) {
        throw new InvalidArchiveException(e.getCause());
      } else {
        LOGGER.error(e.getMessage(), e);
        throw new DecompressionException(e);
      }
    }
  }
}
