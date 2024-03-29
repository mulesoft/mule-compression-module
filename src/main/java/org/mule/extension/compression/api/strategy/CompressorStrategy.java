/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.api.strategy;

import org.mule.extension.compression.internal.error.exception.CompressionException;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.runtime.operation.Result;

import java.io.InputStream;

/**
 * Provides the capability to compress data in some compression format.
 *
 * @since 2.0
 */
public interface CompressorStrategy {

  /**
   * Compresses an incoming data content. Returns a new compressed Stream
   */
  Result<InputStream, Void> compress(TypedValue<InputStream> data) throws CompressionException;

}
