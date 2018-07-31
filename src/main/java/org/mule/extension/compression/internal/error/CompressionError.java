/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.internal.error;

import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

/**
 * List the {@link ErrorTypeDefinition error definitions} for this module
 *
 * @since 1.0
 */
public enum CompressionError implements ErrorTypeDefinition<CompressionError> {

  /**
   * The content to be decompressed is not a valid archive
   */
  INVALID_ARCHIVE,

  /**
   * Error that is thrown when an archive with multiple entries is passed to the decompress operation.
   */
  TOO_MANY_ENTRIES,

  /**
   * Error occurred while trying to decompress
   */
  COULD_NOT_DECOMPRESS,

  /**
   * Error occurred while trying to compress
   */
  COULD_NOT_COMPRESS
}
