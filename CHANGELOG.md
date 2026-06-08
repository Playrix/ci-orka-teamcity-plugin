# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [3.1.0-ci] - 2026-06-08

First semantically versioned release of the fork (previously built as
`SNAPSHOT-<timestamp>`). Versioned above the upstream `2.0.1` so TeamCity
recognizes it as newer.

### Fixed

- **Orphaned VMs after failed deployments.** When a VM deployment failed during
  an abnormal situation (network error, HTTP 504 gateway timeout), the plugin
  removed the instance from in-memory tracking without deleting the VM from
  Orka. The VM kept running, auto-started its agent, and the agent hung in the
  "Unauthorized" tab in TeamCity. Deployment failures where the VM may have been
  created (504, gateway/proxy errors, network exceptions, or a VM missing right
  after a "successful" deploy) now keep the instance tracked under its real Orka
  name and mark it for termination, so `RemoveFailedInstancesTask` verifies the
  VM via the Orka API and deletes the orphan. Explicit quota rejections
  (`Cannot deploy more than ...`) still simply drop tracking, since no VM was
  created.

### Added

- **Periodic orphaned VM cleanup task.** A new background task
  (`OrphanedVmCleanupTask`) periodically scans Orka for VMs tagged with this
  profile's `tc_profile_id` metadata that are no longer tracked by the plugin
  and are not currently being deployed, and deletes them as a safety net against
  leaked VMs. New internal properties:
  - `teamcity.cloud.orka.orphanCleanup.enabled` — enable/disable the task
    (default: `true`).
  - `teamcity.cloud.orka.orphanCleanup.intervalMinutes` — scan interval in
    minutes (default: `15`).

[Unreleased]: https://github.com/Playrix/ci-orka-teamcity-plugin/compare/v3.1.0-ci...HEAD
[3.1.0-ci]: https://github.com/Playrix/ci-orka-teamcity-plugin/releases/tag/v3.1.0-ci
