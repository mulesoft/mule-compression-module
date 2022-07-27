/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.internal;

import static java.lang.System.getProperty;
import static org.mule.extension.compression.internal.CompressionExtension.ZIP_MEDIA_TYPE;
import static org.mule.runtime.api.metadata.DataType.INPUT_STREAM;
import static org.mule.runtime.core.api.util.FileUtils.copyStreamToFile;

import com.google.common.base.Preconditions;
import org.mule.extension.compression.internal.error.exception.CompressionException;
import org.mule.extension.compression.internal.zip.TempZipFile;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.scheduler.Scheduler;
import org.mule.runtime.api.scheduler.SchedulerService;
import org.mule.runtime.api.transformation.TransformationService;
import org.mule.runtime.extension.api.runtime.operation.Result;

import java.io.*;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.print.attribute.standard.Compression;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.scalactic.Bool;

/**
 * Manages resources necessary for performing compression operations
 *
 * @since 2.0.2
 */
public class CompressionManager implements Startable, Stoppable {

  private static final File TEMP_DIR = new File(getProperty("java.io.tmpdir"));
  private static final Random RANDOM = new Random();

  @Inject
  private SchedulerService schedulerService;

  @Inject
  private TransformationService transformationService;

  private Scheduler compressionScheduler;

  @Override
  public void start() throws MuleException {
    compressionScheduler = schedulerService.cpuIntensiveScheduler();
  }

  @Override
  public void stop() throws MuleException {
    compressionScheduler.stop();
    compressionScheduler = null;
  }

  public Result<InputStream, Void> asyncArchive(Map<String, TypedValue<InputStream>> entries, Boolean forceZip64) {
    try {
      //  PipedInputStream in = new PipedInputStream();
      InputStreamWithFinalExceptionCheck inWithException = new InputStreamWithFinalExceptionCheck();
      PipedOutputStream out = new PipedOutputStream(inWithException);

      compressionScheduler.submit(() -> {
        try {
          archive(entries, out, forceZip64);
        } catch (CompressionException e) {
          System.out.println("catch a exception");
          inWithException.fail(new IOException(e.getMessage()));
        } finally {
          inWithException.countDown();
        }
      });

      return Result.<InputStream, Void>builder()
          .output(inWithException)
          .mediaType(ZIP_MEDIA_TYPE)
          .build();
    } catch (CompressionException e) {
      throw e;
    } catch (Throwable t) {
      throw new CompressionException(t);
    }
  }

  /**
   * Receives a ZIP content and creates a temporal physical {@link TempZipFile file} for it
   *
   * @param inputStream the zip content
   * @return a {@link TempZipFile}
   * @throws IOException in case of IO issues
   */
  public TempZipFile toTempZip(InputStream inputStream) throws IOException {
    return new TempZipFile(toTempFile(inputStream));
  }

  private File toTempFile(InputStream inputStream) throws IOException {
    String fileName = "mule-compression-buffer" + RANDOM.nextLong() + ".tmp";
    File file = new File(TEMP_DIR, fileName);
    copyStreamToFile(inputStream, file);

    return file;
  }

  /**
   * Creates an archive of the given entries
   *
   * @param entries the entries to archive
   * @param out     the {@link OutputStream} in which the compressed content is going to be written
   */
  private void archive(Map<String, TypedValue<InputStream>> entries, OutputStream out, Boolean forceZip64)
      throws CompressionException {
    try (ZipArchiveOutputStream zip = new ZipArchiveOutputStream(out)) {

      Set<String> strings = entries.keySet();
      for (String key : strings) {
        addEntry(zip, key, entries.get(key), transformationService, forceZip64);
      }

    } catch (Exception e) {
      throw new CompressionException(e.getCause());
    }
  }

  private void addEntry(ZipArchiveOutputStream zip,
                        String name,
                        TypedValue<InputStream> entryContent,
                        TransformationService transformationService,
                        boolean forceZip64)
      throws IOException {
    try {
      ZipArchiveEntry newEntry = new ZipArchiveEntry(name);
      if (forceZip64) {
        zip.setUseZip64(Zip64Mode.Always);
      }
      zip.putArchiveEntry(newEntry);
      System.out.println("dentro de schedyler" + Thread.currentThread().getName());
      byte[] buffer = new byte[1024];
      int length;
      InputStream content = getContent(name, entryContent, transformationService);

      while ((length = content.read(buffer)) >= 0) {
        zip.write(buffer, 0, length);
      }
      zip.closeArchiveEntry();
    } catch (Exception e) {
      throw new CompressionException(e);
    }
  }

  private InputStream getContent(String name, TypedValue<?> entryContent, TransformationService transformationService) {
    try {
      Object value = entryContent.getValue();
      if (value instanceof InputStream) {
        return (InputStream) value;
      }
      return (InputStream) transformationService.transform(value, entryContent.getDataType(), INPUT_STREAM);
    } catch (Exception e) {
      throw new CompressionException("cannot archive entry [" + name + "], content cannot be transformed to InputStream");
    }
  }

  private static final class InputStreamWithFinalExceptionCheck extends PipedInputStream {

    private final AtomicReference<IOException> exception = new AtomicReference<>(null);
    private final CountDownLatch complete = new CountDownLatch(1);

    @Override
    public int read(byte[] b) throws IOException {
      if (exception.get() != null) {
        System.out.println("READ have to throw exception");
        throw exception.get();
      }
      return super.read(b);
    }

    @Override
    public synchronized int read() throws IOException {
      if (exception.get() != null) {
        System.out.println("READ have to throw exception");
        throw exception.get();
      }
      return super.read();
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) throws IOException {
      if (exception.get() != null) {
        System.out.println("READ have to throw exception");
        throw exception.get();
      }
      return super.read(b, off, len);
    }

    public void fail(final IOException e) {
      exception.set(e);
    }

    public void countDown() {
      complete.countDown();
    }
  }
}
