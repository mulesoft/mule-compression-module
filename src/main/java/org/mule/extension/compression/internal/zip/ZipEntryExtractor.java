/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.internal.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

/**
 * Reads the current entry of a {@link ZipInputStream} until it ends, and creates a new {@link InputStream} for that single entry.
 *
 * @since 2.0
 */
public class ZipEntryExtractor {

  public InputStream extractEntry(ZipInputStream zip) throws IOException {
    try (ByteArrayOutputStream holder = new ByteArrayOutputStream()) {
      final byte[] buffer = new byte[1024];
      int length;
      while ((length = zip.read(buffer, 0, buffer.length)) >= 0) {
        holder.write(buffer, 0, length);
      }
      return new ByteArrayInputStream(holder.toByteArray());
    }
  }
}
