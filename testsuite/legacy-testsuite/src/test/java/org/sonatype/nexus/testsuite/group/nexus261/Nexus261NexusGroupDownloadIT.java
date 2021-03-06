/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2008-2015 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.testsuite.group.nexus261;

import java.io.File;
import java.util.Date;

import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.integrationtests.TestContainer;
import org.sonatype.nexus.test.utils.FileTestingUtils;

import org.apache.maven.index.artifact.Gav;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests to make sure an artifact deployed in multiple repositories will respect the group order.
 */
public class Nexus261NexusGroupDownloadIT
    extends AbstractNexusIntegrationTest
{

  @BeforeClass
  public static void setSecureTest() {
    TestContainer.getInstance().getTestContext().setSecureTest(true);
  }

  @Test
  public void downloadArtifact()
      throws Exception
  {
    Gav gav =
        new Gav(this.getTestId(), "release-jar", "1", null, "jar", 0, new Date().getTime(),
            "Release Jar", false, null, false, null);

    File artifact = downloadArtifactFromGroup("nexus-test", gav, "./target/downloaded-jars");

    assertTrue(artifact.exists());

    File originalFile =
        this.getTestResourceAsFile("projects/" + gav.getArtifactId() + "/" + gav.getArtifactId() + "."
            + gav.getExtension());

    Assert.assertTrue(FileTestingUtils.compareFileSHA1s(originalFile, artifact));

  }
}
