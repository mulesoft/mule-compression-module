/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.internal;

import static org.mule.extension.compression.internal.CompressionExtension.ZIP_MEDIA_TYPE;
import static org.mule.runtime.core.api.util.FileUtils.copyStreamToFile;
import static org.mule.runtime.api.metadata.DataType.INPUT_STREAM;
import static java.lang.System.getProperty;

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

import javax.inject.Inject;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.Zip64Mode;

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

      PipedInputStream pipe = new PipedInputStream();
      PipedOutputStream out = new PipedOutputStream(pipe);

      compressionScheduler.submit(() -> archive(entries, out, forceZip64));

      return Result.<InputStream, Void>builder()
          .output(pipe)
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
  private void archive(Map<String, TypedValue<InputStream>> entries, OutputStream out, Boolean forceZip64) {
    try (ZipArchiveOutputStream zip = new ZipArchiveOutputStream(out)) {
      entries.forEach((name, content) -> addEntry(zip, name, content, transformationService, forceZip64));

    } catch (IOException e) {
      throw new CompressionException(e);
    }
  }

  private void addEntry(ZipArchiveOutputStream zip,
                        String name,
                        TypedValue<InputStream> entryContent,
                        TransformationService transformationService,
                        boolean forceZip64) {
    try {
      ZipArchiveEntry newEntry = new ZipArchiveEntry(name);
      if (forceZip64) {
        zip.setUseZip64(Zip64Mode.Always);
      }
      zip.putArchiveEntry(newEntry);

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

}
