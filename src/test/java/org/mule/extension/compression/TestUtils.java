/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression;

import java.io.IOException;
import java.io.InputStream;

/**
 * Testing utilities. Used primarily in the munit tests.
 *
 * @since 2.0.2
 */
public class TestUtils {

  /**
   * Returns a fake input stream of a given size
   *
   * @param size how many bytes should the stream have
   * @return an {@link InputStream}
   */
  public static InputStream inputStream(int size) {
    return new FakeInputStream(size);
  }

  private static class FakeInputStream extends InputStream {

    private final int size;
    private int count = 0;

    public FakeInputStream(int size) {
      this.size = size;
    }

    @Override
    public int read() throws IOException {
      if (count++ >= size) {
        return -1;
      }
      return (int) 'a';
    }
  }
}
