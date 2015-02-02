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

package org.sonatype.nexus.wonderland.rest;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.sonatype.nexus.wonderland.WonderlandPlugin;
import org.sonatype.siesta.Resource;
import org.sonatype.sisu.goodies.common.ComponentSupport;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.subject.Subject;

/**
 * Session resource.
 *
 * @since 3.0
 */
@Named
@Singleton
@Path(SessionResource.RESOURCE_URI)
class SessionResource
    extends ComponentSupport
    implements Resource
{
  public static final String RESOURCE_URI = WonderlandPlugin.REST_PREFIX + "/session";

  @POST
  @RequiresAuthentication
  public void create() {
    Subject subject = SecurityUtils.getSubject();
    log.debug("Created session: {}", subject.getPrincipal());
    // Shiro handles the details here automatically
  }

  @DELETE
  @RequiresUser
  public void delete() {
    Subject subject = SecurityUtils.getSubject();
    log.debug("Delete session: {}", subject.getPrincipal());
    subject.logout();
  }
}
