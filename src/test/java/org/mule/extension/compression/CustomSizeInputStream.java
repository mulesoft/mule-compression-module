/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.extension.compression;

import java.io.InputStream;

/**
 * The purpose of this class is returns integer in the read() method,
 * as many times as indicated in the constructor at the moment of the instance.
 *
 *  * @since 2.2.0
 */
public class CustomSizeInputStream extends InputStream {

  private long size;
  private static final int ANY_NUMBER = 5;

  public CustomSizeInputStream(long size) {
    this.size = size;
  }

  @Override
  public int read() {
    if (size > 0) {
      size--;
      return ANY_NUMBER;
    } else {
      return -1;
    }
  }
}
