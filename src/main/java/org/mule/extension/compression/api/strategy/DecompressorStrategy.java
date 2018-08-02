/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.api.strategy;

import org.mule.extension.compression.internal.error.exception.DecompressionException;
import org.mule.extension.compression.internal.error.exception.TooManyEntriesException;
import org.mule.runtime.api.metadata.TypedValue;

import java.io.InputStream;

/**
 * Provides the capability to decompress a compressed content in some format.
 *
 * @since 2.0
 */
public interface DecompressorStrategy {

  /**
   * Decompresses a compressed content.
   * <p>
   * For compressed archives this will only work with single entry archives. e.g. a zip file with more than 2 entries
   * will end up in a {@link TooManyEntriesException}.
   */
  InputStream decompress(TypedValue<InputStream> compressed) throws DecompressionException, TooManyEntriesException;

}
