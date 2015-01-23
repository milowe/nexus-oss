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
package org.sonatype.nexus.repository.security;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.sonatype.configuration.validation.ValidationResponse;
import org.sonatype.security.model.CPrivilege;
import org.sonatype.security.realms.privileges.PrivilegeDescriptor;
import org.sonatype.security.realms.privileges.PrivilegePropertyDescriptor;
import org.sonatype.security.realms.validator.SecurityValidationContext;

import com.google.common.base.Strings;

import static com.google.common.base.Preconditions.checkNotNull;

// TODO: Move to proper module
// NOTE: Not using existing AbstractPrivilegeDescriptor to investigate divorcing from legacy

/**
 * Support for {@link PrivilegeDescriptor} implementations.
 *
 * @since 3.0
 */
public abstract class PrivilegeDescriptorSupport
  implements PrivilegeDescriptor
{
  private final String type;

  public PrivilegeDescriptorSupport(final String type) {
    this.type = checkNotNull(type);
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public String getName() {
    // FIXME: PrivilegeDescriptor.getName() is no longer exposed, but not yet removed
    return "UNUSED";
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" +
        "type='" + type + '\'' +
        '}';
  }

  @Override
  public List<PrivilegePropertyDescriptor> getPropertyDescriptors() {
    // FIXME: These are not presently required, but could still be useful to properly support extensibility in the UI
    return Collections.emptyList();
  }

  @Override
  public ValidationResponse validatePrivilege(final CPrivilege privilege,
                                              final SecurityValidationContext context,
                                              final boolean update)
  {
    // FIXME: For now ignore validation
    return new ValidationResponse();
  }

  @Override
  @Nullable
  public String buildPermission(final CPrivilege privilege) {
    // FIXME: This is a poor design, this check should be done by caller?
    if (!getType().equals(privilege.getType())) {
      return null;
    }

    return formatPermission(privilege);
  }

  /**
   * Format permission string for given privilege.
   */
  protected abstract String formatPermission(final CPrivilege privilege);

  /**
   * Helper to read a privilege property and return default-value if unset or empty.
   */
  protected String readProperty(final CPrivilege privilege, final String name, final String defaultValue) {
    String value = privilege.getProperty(name);
    if (Strings.nullToEmpty(value).isEmpty()) {
      value = defaultValue;
    }
    return value;
  }
}
