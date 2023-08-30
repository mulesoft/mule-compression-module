/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.api.strategy;

import org.mule.extension.compression.internal.error.exception.DecompressionException;
import org.mule.runtime.api.metadata.TypedValue;

import java.io.InputStream;
import java.util.Map;

/**
 * Provides the capability to extract the entries of a compressed archive in some format.
 *
 * @since 2.0
 */
public interface ExtractorStrategy {

  /**
   * Extracts all the entries of an compressed archive and returns a map with the name of the entry as key and
   * the proper data content of the entry as value.
   */
  Map<String, InputStream> extract(TypedValue<InputStream> archive) throws DecompressionException;
}
