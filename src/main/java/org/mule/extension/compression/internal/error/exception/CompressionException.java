/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.internal.error.exception;

import org.mule.extension.compression.internal.error.CompressionError;
import org.mule.runtime.extension.api.exception.ModuleException;

import static org.mule.extension.compression.internal.error.CompressionError.COULD_NOT_COMPRESS;

/**
 * A {@link ModuleException} to signal that an unexpected error occurred while trying to archive a content.
 * <p>
 * This exception will be associated to a {@link CompressionError#COULD_NOT_COMPRESS} error type.
 *
 * @since 1.0
 */
public class CompressionException extends ModuleException {

  public CompressionException(Throwable cause) {
    super("Unexpected error occur while trying to archive: " + cause.getMessage(), COULD_NOT_COMPRESS, cause);
  }

  public CompressionException(String message) {
    super("Unexpected error occur while trying to archive: " + message, COULD_NOT_COMPRESS);
  }
}
