/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.extension.compression;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class FakeInputStream extends InputStream {

  long size;
  final Random random = new Random();

  public FakeInputStream(long size) {
    this.size = size;
  }

  @Override
  public int read() throws IOException {
    if (size > 0) {
      size--;
      return 5;
    } else {
      return -1;
    }
  }
}
