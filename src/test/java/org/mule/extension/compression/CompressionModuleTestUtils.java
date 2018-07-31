/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression;

import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.core.api.util.IOUtils;

import static java.lang.Thread.currentThread;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.mule.runtime.api.metadata.DataType.TEXT_STRING;

public class CompressionModuleTestUtils {

  public static final int DATA_SIZE = 1024;
  public static final String TEST_DATA = randomAlphabetic(DATA_SIZE);

  public static String FILE_TXT_NAME = "file.txt";
  public static String FILE_TXT_DATA = getFileTxtData();

  private static String getFileTxtData() {
    return IOUtils.toString(currentThread().getContextClassLoader().getResourceAsStream(FILE_TXT_NAME));
  }

  public static <T> TypedValue<T> asTextTypedValue(T content) {
    return new TypedValue<>(content, TEXT_STRING);
  }
}
