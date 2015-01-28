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
package org.sonatype.security.authorization;

import org.apache.shiro.authz.permission.WildcardPermission;

/**
 * {@link WildcardPermission} which caches {@link #hashCode} for improved performance.
 *
 * @since 3.0
 */
public class WildcardPermission2
  extends WildcardPermission
{
  private final int cachedHash;

  public WildcardPermission2(final String wildcardString) {
    super(wildcardString);
    this.cachedHash = super.hashCode();
  }

  @Override
  public int hashCode() {
    return cachedHash;
  }
}
