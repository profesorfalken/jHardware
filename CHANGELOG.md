# Changelog

All notable changes to jHardware are documented here. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and the project uses
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-11-12

First stable release. Java 8 or newer is required. The public/protected API is
binary and source compatible with 0.8.6; serialized snapshots are not. The
built-in providers never escalate privileges.

### Added

- `HardwareInfoService`, a configurable entry point that accepts an `OsFamily`
  and a `CommandRunner`.
- `CommandRunner`, `CommandResult` and `SystemCommandRunner` with a default
  10-second timeout, separate stdout/stderr, UTF-8 decoding and exit codes.
- `getWarnings()` on every component snapshot, with an unmodifiable list of
  non-fatal problems.
- `getFullInfo()` on every component snapshot, including network, display and
  graphics-card models.
- Normalized memory accessors in bytes (`getTotalMemoryBytes`, etc.) while
  keeping the legacy platform strings.
- `UnsupportedOperatingSystemException` with an actionable message.
- MAC address and coarse interface type in `NetworkInterfaceInfo`.
- Serialization support for `Display` and `GraphicsCard`, plus
  `serialVersionUID` in every model.
- JUnit 5 test suite (110 tests) with Linux and Windows parser fixtures,
  serialization coverage and command-runner timeout tests.
- GitHub Actions CI for Linux and Windows on JDK 8, 11, 17 and 21.
- Source and Javadoc jars attached to the Maven build.

### Changed

- Minimum Java version is 8; compilation targets 1.8 source and bytecode.
- Linux network enumeration uses the JDK and `/sys/class/net`, removing the
  `ifconfig` dependency.
- Linux display enumeration falls back to DRM sysfs when `xrandr` is absent.
- Linux graphics-card enumeration falls back from `lshw` to `lspci` to DRM
  sysfs.
- Linux OS information reads `/etc/os-release` first and uses `/proc/stat`
  `btime` for the boot time instead of `last -x`.
- BIOS and motherboard information read the DMI sysfs files first and only try
  `dmidecode` as an unprivileged fallback.
- Windows BIOS uses `Win32_BIOS` (with `CIM_BIOSElement` fallback) and formats
  its release date.
- Windows memory uses `Win32_ComputerSystem.TotalPhysicalMemory`, with
  `Win32_PhysicalMemory` and `TotalVisibleMemorySize` fallbacks.
- Windows OS boot time is parsed from the CIM datetime format, including its
  signed minute offset.
- Windows display and graphics-card parsers handle multiple monitors, deduplicate
  cards and tolerate missing fields.
- `DirectXInfoLoader` is lazy, cached, bounded by a deadline and parses XML
  with DTDs and external entities disabled.
- `HardwareInfoUtils` no longer logs to `java.util.logging`; failures are
  returned as empty values or warnings.
- The Maven build pins plugin versions, enforces Java 8+ and Maven 3.8+ and
  uses Surefire 3.5.x.
- Dependencies updated to WMI4Java 1.6.3, jSensors 3.1.0, jPowerShell 3.2.0 and
  JUnit 5.

### Fixed

- `NullPointerException` in `UnixDisplayInfo` when `xrandr` was missing.
- `NullPointerException` in `AbstractDisplayInfo` when a display had no
  `available_res` entry.
- `NullPointerException` when `available_res` was missing on Windows
  multi-monitor systems.
- `IllegalStateException` in `HardwareInfoUtils.extractText` when a regex did
  not match.
- `NoSuchElementException` in `getSingleValueFromFile` for missing files.
- `NullPointerException` in `isSudo` when `sudo` was not installed (the method
  is deprecated and no longer used internally).
- Off-by-one error that skipped the last GPU in the Linux implementation.
- The `last` output parser could throw `StringIndexOutOfBoundsException`; it was
  replaced by `/proc/stat`.
- `null` keys and null `Manufacturer`/`Name`/`Version` from `lsb_release` when
  the command was absent.
- Windows month off-by-one and 12-hour parsing in `LastBootUpTime`.
- `ArrayIndexOutOfBoundsException` when parsing unknown CPU descriptions.
- `NumberFormatException`/`NullPointerException` from missing count keys in the
  display, graphics-card and network assemblers.
- `NotSerializableException` for `DisplayInfo` and `GraphicsCardInfo`.
- Infinite dxdiag polling loop and XML parser NPEs.
- Boot-time and thermal-zone selection now prefers the CPU package sensor over
  the generic ACPI zone.
- jSensors failures (including JNA `LinkageError`) degrade gracefully instead
  of breaking CPU/GPU snapshots.

### Removed

- `.travis.yml`; CI now uses GitHub Actions.
- Internal use of `sudo`, `ifconfig`, `xrandr`, `lshw`, `lsb_release` and
  `last -x` as hard requirements.
- Direct `java.util.logging` output from the library.
- JUnit 4.10.

### Deprecated

- `HardwareInfoUtils.readFile`, `getSingleValueFromFile` and `executeCommand`.
  Use `readLines`, `readFirstLine` and `CommandRunner` instead.
- `HardwareInfoUtils.isSudo`, retained only for 0.8.x compatibility. The
  built-in providers never escalate privileges.

## [0.8.6] - 2018-03-31

Last release of the 0.x series. See the Git history for details.
