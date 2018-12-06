/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.internal;

import static java.lang.System.getProperty;
import static org.mule.runtime.api.util.Preconditions.checkState;
import static org.mule.runtime.core.api.util.FileUtils.copyStreamToFile;
import org.mule.extension.compression.internal.zip.TempZipFile;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
import org.mule.runtime.api.scheduler.Scheduler;
import org.mule.runtime.api.scheduler.SchedulerService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javax.inject.Inject;

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

  /**
   * @return a {@link Scheduler} for performing asynchronous compression operations
   */
  public Scheduler getCompressionScheduler() {
    checkState(compressionScheduler != null, "Compressor not started or stopped");
    return compressionScheduler;
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

}
