/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.compression.internal;

import org.mule.extension.compression.api.CompressionError;
import org.mule.extension.compression.internal.zip.ZipOperations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;

/**
 * A module which provides functionality for compressing and uncompressing files.
 *
 * @since 1.0
 */
@Extension(name = "Compression")
@Operations(ZipOperations.class)
@ErrorTypes(CompressionError.class)
public class CompressionExtension {

}
