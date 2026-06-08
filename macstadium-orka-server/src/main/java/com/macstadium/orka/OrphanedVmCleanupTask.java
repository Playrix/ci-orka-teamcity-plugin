package com.macstadium.orka;

import com.intellij.openapi.diagnostic.Logger;

import jetbrains.buildServer.log.Loggers;

/**
 * Periodically asks the {@link OrkaCloudClient} to reap orphaned Orka VMs - VMs that belong to the
 * profile (matched via {@code tc_profile_id} metadata) but are no longer tracked by the plugin and are
 * not currently being deployed. This is the safety net against VMs leaked during abnormal situations
 * (e.g. network failures / HTTP 504 during deployment) whose auto-started agents would otherwise hang
 * in the "Unauthorized" state.
 */
public class OrphanedVmCleanupTask implements Runnable {
    private static final Logger LOG = Logger.getInstance(Loggers.CLOUD_CATEGORY_ROOT + OrkaConstants.TYPE);
    private final OrkaCloudClient client;

    public OrphanedVmCleanupTask(OrkaCloudClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            this.client.cleanupOrphanedVms();
        } catch (Exception e) {
            LOG.warn("Orphaned VM cleanup task failed: " + e.getMessage(), e);
        }
    }
}
