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
/*global Ext*/

/**
 * Expire session window.
 *
 * @since 3.0
 */
Ext.define('NX.view.ExpireSession', {
  extend: 'Ext.window.Window',
  alias: 'widget.nx-expire-session',

  title: NX.I18n.get('GLOBAL_EXPIRE_TITLE'),

  layout: 'fit',
  autoShow: true,
  modal: true,
  constrain: true,
  width: 400,
  closable: false,

  /**
   * @protected
   */
  initComponent: function () {
    var me = this;

    Ext.apply(this, {
      items: [
        {
          xtype: 'label',
          id: 'expire',
          text: NX.I18n.get('GLOBAL_EXPIRE_HELP'),
          style: {
            'color': 'red',
            'font-size': '20px',
            'margin': '108px'
          }
        }
      ],
      buttons: [
        { text: NX.I18n.get('GLOBAL_EXPIRE_CANCEL_BUTTON'), action: 'cancel' }
      ]
    });

    me.callParent();
  }

});
