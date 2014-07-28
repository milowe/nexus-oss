/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2014 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.internal.orient;

import org.sonatype.nexus.orient.DatabasePool;
import org.sonatype.sisu.goodies.lifecycle.LifecycleSupport;
import org.sonatype.sisu.goodies.lifecycle.Lifecycles;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link DatabasePool} implementation.
 *
 * @since 3.0
 */
public class DatabasePoolImpl
  extends LifecycleSupport
  implements DatabasePool
{
  private final String name;

  private final ODatabaseDocumentPool delegate;

  public DatabasePoolImpl(final ODatabaseDocumentPool pool, final String name) {
    this.delegate = checkNotNull(pool);
    this.name = checkNotNull(name);
  }

  @Override
  public String getName() {
    return name;
  }

  // promote to public
  @Override
  public boolean isStarted() {
    return super.isStarted();
  }

  @Override
  protected void doStop() throws Exception {
    delegate.close();
  }

  @Override
  public ODatabaseDocumentTx acquire() {
    ensureStarted();

    return delegate.acquire();
  }

  @Override
  public void close() {
    Lifecycles.stop(this);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" +
        "name='" + name + '\'' +
        '}';
  }
}