/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2008-2015 Sonatype Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.yum.internal.createrepo

import groovy.transform.ToString

/**
 * @since 3.0
 */
@ToString(includePackage = false, includeNames = true)
class YumPackage
{

  String checksum
  String name
  String arch
  String version
  String epoch
  String release
  String summary
  String description
  String url
  Integer time_file
  Integer time_build
  String rpm_license
  String rpm_vendor
  String rpm_group
  String rpm_buildhost
  String rpm_sourcerpm
  Integer rpm_header_start
  Integer rpm_header_end
  String rpm_packager
  Integer size_package
  Integer size_installed
  Integer size_archive
  String location_href
  String location_base
  String checksum_type

  List<Entry> provides
  List<Entry> requires
  List<Entry> conflicts
  List<Entry> obsoletes

  List<File> files
  List<ChangeLog> changes

  @ToString(includePackage = false, includeNames = true)
  static class Entry {
    String name
    Integer flags
    String epoch
    String version
    String release
    boolean pre
  }

  @ToString(includePackage = false, includeNames = true)
  static class File {
    String name
    FileType type
    String primary
  }

  static enum FileType {
    file, dir, ghost
  }

  @ToString(includePackage = false, includeNames = true)
  static class ChangeLog {
    String author
    Integer date
    String text
  }

}
