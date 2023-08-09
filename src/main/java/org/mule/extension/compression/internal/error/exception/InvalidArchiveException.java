/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.internal.error.exception;

import static org.mule.extension.compression.internal.error.CompressionError.INVALID_ARCHIVE;
import org.mule.extension.compression.internal.error.CompressionError;
import org.mule.runtime.extension.api.exception.ModuleException;

/**
 * A {@link ModuleException} to signal that an invalid archive was passed to a decompression/extract operation.
 * <p>
 * This exception will be associated to a {@link CompressionError#INVALID_ARCHIVE} error type
 *
 * @since 2.0
 */
public class InvalidArchiveException extends DecompressionException {

  public InvalidArchiveException(Throwable cause) {
    super("The provided archive is not valid - " + cause.getMessage(), INVALID_ARCHIVE, cause);
  }

  public InvalidArchiveException(String message) {
    super(message, INVALID_ARCHIVE);
  }
}
