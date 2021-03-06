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
/*global Ext, NX*/

/**
 * Upload file entry panel.
 *
 * @since 3.0
 */
Ext.define('NX.coreui.view.upload.UploadFileEntry', {
  extend: 'Ext.panel.Panel',
  alias: 'widget.nx-coreui-upload-file-entry',

  /**
   * @cfg entry name
   */
  entryName: 'file',

  initComponent: function() {
    var me = this;

    me.items = [
      {
        xtype: 'panel',
        layout: {
          type: 'hbox',
          align: 'bottom'
        },
        style: {
          borderTop: '1px solid #F0F0F0'
        },
        margin: '0 0 5 0',
        defaults: {
          margin: '5 5 0 0'
        },
        items: [
          me.uploader = {
            xtype: 'fileuploadfield',
            fieldLabel: Ext.String.capitalize(me.entryName),
            helpText: 'Path of the ' + me.entryName + ' to be uploaded.',
            name: me.name,
            allowBlank: false,
            clearOnSubmit: false,
            buttonConfig: {
              text: undefined, glyph: 'xf016@FontAwesome' /* fa-file-o */
            }
          },
          { xtype: 'button', glyph: 'xf056@FontAwesome' /* fa-minus-circle */, action: 'delete' }
        ]
      }
    ];

    me.callParent(arguments);
  }

});
