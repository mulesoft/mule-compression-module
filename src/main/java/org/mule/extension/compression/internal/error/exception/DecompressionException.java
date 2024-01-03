/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.internal.error.exception;

import static org.mule.extension.compression.internal.error.CompressionError.COULD_NOT_DECOMPRESS;

import org.mule.extension.compression.internal.error.CompressionError;
import org.mule.runtime.extension.api.exception.ModuleException;

/**
 * A {@link ModuleException} to signal that an unexpected error occurred while trying to decompress a content.
 * <p>
 * This exception will be associated to a {@link CompressionError#COULD_NOT_DECOMPRESS} error type
 *
 * @since 1.0
 */
public class DecompressionException extends ModuleException {

  public DecompressionException(Throwable cause) {
    super("Unexpected error occur while trying to decompress: " + cause.getMessage(), COULD_NOT_DECOMPRESS, cause);
  }

  public DecompressionException(String message, CompressionError error) {
    super("Error decompressing: " + message, error);
  }

  public DecompressionException(String message, CompressionError error, Throwable cause) {
    super("Error decompressing: " + message, error, cause);
  }
}
