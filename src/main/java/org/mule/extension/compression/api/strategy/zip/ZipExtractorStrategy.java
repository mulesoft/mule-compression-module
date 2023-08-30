/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.api.strategy.zip;

import static org.apache.commons.compress.archivers.zip.ZipFile.closeQuietly;
import org.mule.extension.compression.api.strategy.ExtractorStrategy;
import org.mule.extension.compression.internal.CompressionManager;
import org.mule.extension.compression.internal.PostActionInputStreamWrapper;
import org.mule.extension.compression.internal.error.exception.DecompressionException;
import org.mule.extension.compression.internal.error.exception.InvalidArchiveException;
import org.mule.extension.compression.internal.zip.TempZipFile;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.exception.ModuleException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipException;

import javax.inject.Inject;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

/**
 * A Zip extractor.
 *
 * @since 2.0
 */
@DisplayName("Zip")
@Alias("zip-extractor")
public class ZipExtractorStrategy implements ExtractorStrategy {

  @Inject
  private CompressionManager compressionManager;

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, InputStream> extract(TypedValue<InputStream> archive) {
    TempZipFile zip = null;
    try {
      zip = compressionManager.toTempZip(archive.getValue());
      Enumeration<ZipArchiveEntry> entries = zip.getEntries();
      if (!entries.hasMoreElements()) {
        throw new InvalidArchiveException("The provided archive has no entries");
      }

      Map<String, InputStream> files = new HashMap<>();
      FinalCountDown countDown = new FinalCountDown(zip::closeSafely);
      while (entries.hasMoreElements()) {
        ZipArchiveEntry entry = entries.nextElement();
        InputStream stream = new PostActionInputStreamWrapper(zip.getInputStream(entry), countDown::countDown);
        files.put(entry.getName(), stream);
        countDown.countUp();
      }

      return files;
    } catch (ModuleException e) {
      closeQuietly(zip);
      throw e;
    } catch (IOException e) {
      closeQuietly(zip);
      if (e.getCause() instanceof ZipException) {
        throw new InvalidArchiveException(e.getCause());
      } else {
        throw new DecompressionException(e);
      }
    } catch (Exception e) {
      closeQuietly(zip);
      throw new DecompressionException(e);
    }
  }

  private class FinalCountDown {

    private int count = 0;
    private final Runnable action;

    public FinalCountDown(Runnable action) {
      this.action = action;
    }

    public void countUp() {
      count++;
    }

    public void countDown() {
      if (--count == 0) {
        action.run();
      }
    }
  }
}
