/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.internal.error.exception;

import org.mule.extension.compression.internal.error.CompressionError;
import org.mule.runtime.extension.api.exception.ModuleException;

import static org.mule.extension.compression.internal.error.CompressionError.TOO_MANY_ENTRIES;

/**
 * A {@link ModuleException} to signal that an archive with multiple entries was passed to the decompress operation and the
 * module is not able to pic only one entry.
 * <p>
 * This exception will be associated to a {@link CompressionError#TOO_MANY_ENTRIES} error type
 *
 * @since 2.0
 */
public class TooManyEntriesException extends ModuleException {

  public TooManyEntriesException() {
    super("Expected a single entry archive but got multiple, use the extract operation for multiple entry archives",
          TOO_MANY_ENTRIES);
  }
}
