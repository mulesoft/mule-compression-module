/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.mule.runtime.core.api.util.IOUtils.toByteArray;
import org.mule.functional.api.exception.ExpectedError;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.mule.runtime.api.streaming.bytes.CursorStreamProvider;
import org.mule.runtime.core.api.util.IOUtils;

import org.junit.Rule;
import org.junit.Test;

public class ZipTestCase extends MuleArtifactFunctionalTestCase {

  private static final int DATA_SIZE = 1024;
  private static final String TEST_DATA = randomAlphabetic(DATA_SIZE);

  @Rule
  public ExpectedError expected = ExpectedError.none();

  @Override
  protected String getConfigFile() {
    return "zip-config.xml";
  }

  @Test
  public void roundtrip() throws Exception {
    CursorStreamProvider zip = getStream("zip", TEST_DATA);
    byte[] zipBytes = toByteArray(zip.openCursor());
    assertThat(zipBytes.length, lessThan(DATA_SIZE));

    CursorStreamProvider unzipped = getStream("unzip", zip);
    assertThat(IOUtils.toString(unzipped.openCursor()), equalTo(TEST_DATA));
  }


  @Test
  public void unzipInvalidContent() throws Exception {
    expected.expectErrorType("COMPRESSION", "INVALID_ZIP");
    getStream("unzip", TEST_DATA);
  }

  private CursorStreamProvider getStream(String flowName, Object payload) throws Exception {
    return (CursorStreamProvider) flowRunner(flowName)
        .withPayload(payload)
        .keepStreamsOpen()
        .run().getMessage().getPayload().getValue();
  }
}
