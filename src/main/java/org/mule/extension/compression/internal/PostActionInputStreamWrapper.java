/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.internal;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;

/**
 * An {@link InputStreamWrapper} which performs a {@link #postAction} task when the stream is {@link #close() closed}
 *
 * @since 2.0.2
 */
public class PostActionInputStreamWrapper extends InputStreamWrapper {

  private static final Logger LOGGER = getLogger(PostActionInputStreamWrapper.class);

  private final Runnable postAction;

  public PostActionInputStreamWrapper(InputStream delegate, Runnable postAction) {
    super(delegate);
    this.postAction = postAction;
  }

  @Override
  public void close() throws IOException {
    super.close();
    try {
      postAction.run();
    } catch (Exception e) {
      if (LOGGER.isWarnEnabled()) {
        LOGGER.warn("Exceptions was found executing post action task: " + e.getMessage(), e);
      }
    }
  }
}
