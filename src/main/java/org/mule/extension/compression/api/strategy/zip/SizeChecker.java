/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.api.strategy.zip;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class SizeChecker {

  private static final String SMALL_PDF = "small.pdf";
  private static final String LARGE_PDF = "large.pdf";

  public static void calculate(InputStream is) {
    long size = 0;
    int chunk = 0;
    long start = 0;
    try {
      start = System.currentTimeMillis();
      byte[] buffer = new byte[1024];
      while ((chunk = is.read(buffer)) != -1) {
        size += chunk;
      }
    } catch (FileNotFoundException e) {
      System.out.println("Failed to open file stream:" + e.getMessage());
    } catch (IOException e) {
      System.out.println("Failed to read from file stream:" + e.getMessage());
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          System.out.println("Failed to close InputStream: " + e.getMessage());
        }
      }
      long done = System.currentTimeMillis() - start;
      System.out.println(String.format("took %d milliseconds", done));
    }
    System.out.println("size: " + size + " bytes");
  }
}
