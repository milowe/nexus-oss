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
package org.sonatype.nexus;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import javax.crypto.Cipher;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.configuration.ConfigurationException;
import org.sonatype.nexus.configuration.ConfigurationChangeEvent;
import org.sonatype.nexus.configuration.application.NexusConfiguration;
import org.sonatype.nexus.events.EventSubscriberHost;
import org.sonatype.nexus.internal.orient.OrientBootstrap;
import org.sonatype.nexus.proxy.events.NexusInitializedEvent;
import org.sonatype.nexus.proxy.events.NexusStartedEvent;
import org.sonatype.nexus.proxy.events.NexusStoppedEvent;
import org.sonatype.nexus.proxy.events.NexusStoppingEvent;
import org.sonatype.nexus.proxy.registry.RepositoryRegistry;
import org.sonatype.nexus.proxy.repository.ShadowRepository;
import org.sonatype.nexus.scheduling.TaskScheduler;
import org.sonatype.nexus.scheduling.TaskConfiguration;
import org.sonatype.nexus.tasks.SynchronizeShadowsTask;
import org.sonatype.security.SecuritySystem;
import org.sonatype.sisu.goodies.eventbus.EventBus;
import org.sonatype.sisu.goodies.lifecycle.LifecycleSupport;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import org.eclipse.sisu.bean.BeanManager;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This is a component that "boots" Nexus up. See org.sonatype.nexus.web.NexusBooterListener for example.
 *
 * @since 2.7.0
 */
@Singleton
@Named
public class NxApplication
    extends LifecycleSupport
{
  private final EventBus eventBus;

  private final ApplicationStatusSource applicationStatusSource;

  private final NexusConfiguration nexusConfiguration;

  private final SecuritySystem securitySystem;

  private final TaskScheduler taskScheduler;

  private final RepositoryRegistry repositoryRegistry;

  private final EventSubscriberHost eventSubscriberHost;

  private final OrientBootstrap orientBootstrap;

  private final BeanManager beanManager;

  @Inject
  public NxApplication(final EventBus eventBus,
                       final NexusConfiguration nexusConfiguration,
                       final ApplicationStatusSource applicationStatusSource,
                       final SecuritySystem securitySystem,
                       final TaskScheduler taskScheduler,
                       final RepositoryRegistry repositoryRegistry,
                       final EventSubscriberHost eventSubscriberHost,
                       final OrientBootstrap orientBootstrap,
                       final BeanManager beanManager)
  {
    this.eventBus = checkNotNull(eventBus);
    this.applicationStatusSource = checkNotNull(applicationStatusSource);
    this.nexusConfiguration = checkNotNull(nexusConfiguration);
    this.securitySystem = checkNotNull(securitySystem);
    this.taskScheduler = checkNotNull(taskScheduler);
    this.repositoryRegistry = checkNotNull(repositoryRegistry);
    this.eventSubscriberHost = checkNotNull(eventSubscriberHost);
    this.orientBootstrap = checkNotNull(orientBootstrap);
    this.beanManager = checkNotNull(beanManager);
  }

  private void logStarted() {
    StringBuilder buff = new StringBuilder();
    buff.append("\n-------------------------------------------------\n\n");
    buff.append("Started ").append(getNexusNameForLogs());
    buff.append("\n\n-------------------------------------------------");
    log.info(buff.toString());
  }

  @VisibleForTesting
  protected final String getNexusNameForLogs() {
    final StringBuilder msg = new StringBuilder();
    msg.append(applicationStatusSource.getSystemStatus().getAppName());
    msg.append(" ").append(applicationStatusSource.getSystemStatus().getVersion());
    return msg.toString();
  }

  @Override
  protected void doStart() throws Exception {
    if (Cipher.getMaxAllowedKeyLength("AES") == Integer.MAX_VALUE) {
      log.info("Unlimited strength JCE policy detected");
    }

    // register core and plugin contributed subscribers, start dispatching events to them
    eventSubscriberHost.start();

    applicationStatusSource.setState(SystemState.STOPPED);
    applicationStatusSource.getSystemStatus().setInitializedAt(new Date());

    // HACK: Must start database services manually
    orientBootstrap.start();

    eventBus.post(new NexusInitializedEvent(this));

    applicationStatusSource.getSystemStatus().setState(SystemState.STARTING);
    try {
      // force configuration load, validation and probable upgrade if needed
      // applies configuration and notifies listeners
      nexusConfiguration.loadConfiguration(true);
      // essential services
      securitySystem.start();
      securitySystem.getAnonymousUsername();
      nexusConfiguration.createInternals();

      // notify about start other components participating in configuration framework
      eventBus.post(new ConfigurationChangeEvent(nexusConfiguration, null, null));

      applicationStatusSource.getSystemStatus().setLastConfigChange(new Date());
      applicationStatusSource.getSystemStatus().setFirstStart(nexusConfiguration.isConfigurationDefaulted());
      applicationStatusSource.getSystemStatus().setInstanceUpgraded(nexusConfiguration.isInstanceUpgraded());
      applicationStatusSource.getSystemStatus().setConfigurationUpgraded(nexusConfiguration.isConfigurationUpgraded());
      if (applicationStatusSource.getSystemStatus().isFirstStart()) {
        log.info("This is 1st start of new Nexus instance.");
      }
      if (applicationStatusSource.getSystemStatus().isInstanceUpgraded()) {
        log.info("This is an upgraded instance of Nexus.");
      }

      applicationStatusSource.getSystemStatus().setState(SystemState.STARTED);
      applicationStatusSource.getSystemStatus().setStartedAt(new Date());

      synchronizeShadowsAtStartup();

      eventBus.post(new NexusStartedEvent(this));

      logStarted();
    }
    catch (IOException e) {
      applicationStatusSource.getSystemStatus().setState(SystemState.BROKEN_IO);
      applicationStatusSource.getSystemStatus().setErrorCause(e);
      log.error("Could not start Nexus, bad IO exception!", e);
      throw Throwables.propagate(e);
    }
    catch (ConfigurationException e) {
      applicationStatusSource.getSystemStatus().setState(SystemState.BROKEN_CONFIGURATION);
      applicationStatusSource.getSystemStatus().setErrorCause(e);
      log.error("Could not start Nexus, user configuration exception!", e);
      throw Throwables.propagate(e);
    }
  }

  @Override
  protected void doStop() throws Exception {
    applicationStatusSource.getSystemStatus().setState(SystemState.STOPPING);

    // Due to no dependency mechanism in NX for components, we need to fire off a hint about shutdown first
    eventBus.post(new NexusStoppingEvent(this));

    // kill services + notify
    eventBus.post(new NexusStoppedEvent(this));
    eventSubscriberHost.stop();

    nexusConfiguration.dropInternals();
    securitySystem.stop();

    // HACK: Must stop database services manually
    orientBootstrap.stop();

    // dispose of JSR-250
    beanManager.unmanage();

    applicationStatusSource.getSystemStatus().setState(SystemState.STOPPED);
    log.info("Stopped {}", getNexusNameForLogs());
  }

  private void synchronizeShadowsAtStartup() {
    final Collection<ShadowRepository> shadows = repositoryRegistry.getRepositoriesWithFacet(ShadowRepository.class);
    for (ShadowRepository shadow : shadows) {
      if (shadow.isSynchronizeAtStartup()) {
        final TaskConfiguration taskConfiguration = taskScheduler
            .createTaskConfigurationInstance(SynchronizeShadowsTask.class);
        taskConfiguration.setRepositoryId(shadow.getId());
        taskConfiguration.setName("Shadow Sync (" + shadow.getId() + ")");
        taskScheduler.submit(taskConfiguration);
      }
    }
  }
}
