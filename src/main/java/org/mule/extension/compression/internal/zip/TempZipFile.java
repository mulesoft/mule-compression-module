/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.internal.zip;

import static java.lang.String.format;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;

import org.apache.commons.compress.archivers.zip.ZipFile;
import org.slf4j.Logger;

/**
 * A specialization of {@link ZipFile} which is created from a temporal {@link File} which is deleted
 * when {@link #close()} is invoked
 *
 * @since 2.0.2
 */
public class TempZipFile extends ZipFile {

  private static final Logger LOGGER = getLogger(TempZipFile.class);
  private final File tempFile;

  public TempZipFile(File f) throws IOException {
    super(f);
    tempFile = f;
  }

  @Override
  public void close() throws IOException {
    super.close();
    tempFile.delete();
  }

  public void closeSafely() {
    try {
      close();
    } catch (Exception e) {
      if (LOGGER.isWarnEnabled()) {
        LOGGER.warn(format("Exception found while trying to close temporal zip file %s. %s",
                           tempFile.getPath(), e.getMessage()),
                    e);
      }
    }
  }
}
