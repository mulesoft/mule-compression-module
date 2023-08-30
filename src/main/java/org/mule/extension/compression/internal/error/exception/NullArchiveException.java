/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.internal.error.exception;

import org.mule.extension.compression.internal.error.CompressionError;
import org.mule.runtime.extension.api.exception.ModuleException;

import static org.mule.extension.compression.internal.error.CompressionError.INVALID_ARCHIVE;

/**
 * A {@link ModuleException} to signal that a NULL archive was passed to a decompression/extract operation.
 * <p>
 * This exception will be associated to a {@link CompressionError#INVALID_ARCHIVE} error type
 *
 * @since 2.0
 */
public class NullArchiveException extends DecompressionException {

  public NullArchiveException() {
    super("The provided archive is null ", INVALID_ARCHIVE);
  }
}
