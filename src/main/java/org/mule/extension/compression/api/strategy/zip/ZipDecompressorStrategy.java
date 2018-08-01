/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.api.strategy.zip;

import org.mule.extension.compression.api.strategy.DecompressorStrategy;
import org.mule.extension.compression.internal.error.exception.DecompressionException;
import org.mule.extension.compression.internal.error.exception.InvalidArchiveException;
import org.mule.extension.compression.internal.error.exception.TooManyEntriesException;
import org.mule.extension.compression.internal.zip.ZipEntryExtractor;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.exception.ModuleException;

import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

/**
 * A Zip decompressor.
 *
 * @since 2.0
 */
@DisplayName("Zip")
@Alias("zip-decompressor")
public class ZipDecompressorStrategy implements DecompressorStrategy {

  private final ZipEntryExtractor entryExtractor = new ZipEntryExtractor();

  /**
   * {@inheritDoc}
   */
  @Override
  public InputStream decompress(TypedValue<InputStream> compressed) throws DecompressionException {
    try (ZipInputStream zip = new ZipInputStream(compressed.getValue())) {
      ZipEntry entry = zip.getNextEntry();
      if (entry == null) {
        throw new InvalidArchiveException();
      }
      InputStream result = entryExtractor.extractEntry(zip);
      if (zip.getNextEntry() != null) {
        throw new TooManyEntriesException();
      }
      return result;
    } catch (ModuleException e) {
      throw e;
    } catch (ZipException e) {
      throw new InvalidArchiveException(e);
    } catch (Exception e) {
      throw new DecompressionException(e);
    }
  }
}
