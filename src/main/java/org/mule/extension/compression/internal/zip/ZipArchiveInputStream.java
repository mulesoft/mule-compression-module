/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.internal.zip;

import org.mule.extension.compression.internal.error.exception.CompressionException;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.transformation.TransformationService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.mule.runtime.api.metadata.DataType.INPUT_STREAM;

/**
 * An {@link InputStream} that represents a compressed content in the zip format.
 *
 * @since 2.0
 */
public class ZipArchiveInputStream extends InputStream {

  private final InputStream delegate;
  private TransformationService transformationService;

  public ZipArchiveInputStream(Map<String, TypedValue<InputStream>> entries,
                               TransformationService transformationService) {
    //    try {
    this.transformationService = transformationService;
    ByteArrayOutputStream holder = new ByteArrayOutputStream();
    ZipOutputStream zip = new ZipOutputStream(holder);
    entries.forEach((name, content) -> addEntry(zip, name, content));
    //      zip.close();
    //      holder.close();
    this.delegate = new ByteArrayInputStream(holder.toByteArray());
    //    } catch (IOException e) {
    //      throw new CompressionException(e);
    //    }
  }

  private void addEntry(ZipOutputStream zip, String name, TypedValue<?> entryValue) {
    try {
      InputStream content = getContent(name, entryValue);
      ZipEntry newEntry = new ZipEntry(name);
      zip.putNextEntry(newEntry);
      byte[] buffer = new byte[1024];
      int length;
      while ((length = content.read(buffer)) >= 0) {
        zip.write(buffer, 0, length);
      }
      zip.closeEntry();
    } catch (Exception e) {
      throw new CompressionException(e);
    }
  }

  private InputStream getContent(String name, TypedValue<?> entryContent) {
    try {
      Object value = entryContent.getValue();
      if (value instanceof InputStream) {
        return (InputStream) value;
      }
      return (InputStream) transformationService.transform(value, entryContent.getDataType(), INPUT_STREAM);
    } catch (Exception e) {
      throw new CompressionException("cannot compress entry [" + name + "], content cannot be transformed to InputStream");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int read() throws IOException {
    return delegate.read();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int read(byte[] b) throws IOException {
    return delegate.read(b);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    return delegate.read(b, off, len);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long skip(long n) throws IOException {
    return delegate.skip(n);
  }

  @Override
  public int available() throws IOException {
    return delegate.available();
  }

  @Override
  public void close() throws IOException {
    delegate.close();
  }

  @Override
  public synchronized void mark(int readlimit) {
    delegate.mark(readlimit);
  }

  @Override
  public synchronized void reset() throws IOException {
    delegate.reset();
  }

  @Override
  public boolean markSupported() {
    return delegate.markSupported();
  }
}
