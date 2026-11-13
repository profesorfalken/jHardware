# jHardware

![](https://img.shields.io/maven-central/v/org.jhardware/jHardware.svg)
[![CI](https://github.com/profesorfalken/jHardware/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/profesorfalken/jHardware/actions/workflows/ci.yml)
[![Javadoc](https://javadoc.io/badge2/org.jhardware/jHardware/javadoc.svg)](https://javadoc.io/doc/org.jhardware/jHardware)
![](https://img.shields.io/badge/Java-8%2B-orange)
![](https://img.shields.io/github/license/profesorfalken/jHardware.svg)

Cross-platform hardware information for Java. jHardware returns typed snapshots
for the CPU, memory, BIOS, motherboard, operating system, network interfaces,
displays and graphics cards, plus the complete raw payload when you need it.

**Version 1.0.0 targets Java 8 and supports Linux and Windows.** macOS is not
supported and fails with a clear `UnsupportedOperatingSystemException` instead
of returning partial data.

## Highlights

- **No implicit privilege escalation.** The built-in providers never run `sudo`. DMI data is read
  from readable sysfs files; `dmidecode` is only attempted as an unprivileged
  fallback.
- **No fragile net-tools dependency.** Linux network information comes from the
  JDK and sysfs, not from `ifconfig`.
- **Graceful fallbacks.** Displays use `xrandr` when available and DRM sysfs
  otherwise; graphics cards try `lshw`, `lspci` and DRM sysfs in that order.
- **Partial results are explicit.** Every snapshot exposes `getWarnings()`; a
  missing optional tool produces an empty value and a warning, not a
  `NullPointerException`.
- **Bounded external commands.** Commands have a timeout, separate stdout and
  stderr, UTF-8 decoding and captured exit codes.
- **Testable by design.** `HardwareInfoService` accepts an `OsFamily` and a
  `CommandRunner`, so parsers can be tested without spawning processes.

## Supported platforms

| Component | Linux | Windows |
|---|---|---|
| CPU | `/proc/cpuinfo`, jSensors, thermal zones | WMI `Win32_Processor`, jSensors |
| Memory | `/proc/meminfo` | WMI `Win32_OperatingSystem`, `Win32_ComputerSystem`, `Win32_PhysicalMemory` |
| BIOS | `/sys/class/dmi/id`, `dmidecode -t 0` fallback | WMI `Win32_BIOS` |
| Motherboard | `/sys/class/dmi/id`, `dmidecode -t 2` fallback | WMI `Win32_BaseBoard` |
| OS | `/etc/os-release`, `/proc/stat` (`btime`), `lsb_release` fallback | WMI `Win32_OperatingSystem` |
| Network | JDK `NetworkInterface` + `/sys/class/net/*/statistics` | JDK `NetworkInterface` |
| Displays | `xrandr -q`, DRM sysfs fallback | `dxdiag`, WMI fallback |
| Graphics cards | `lshw -C video`, `lspci -mm -nn`, DRM sysfs | `dxdiag`, WMI fallback |

### Linux requirements

`/proc` and `/sys` must be mounted. The following tools are optional and only
improve the result when present:

- `xrandr` for refresh rates (the DRM fallback reports resolutions only).
- `lshw` or `lspci` for graphics-card names (the DRM fallback reports PCI IDs).
- `dmidecode` for BIOS/motherboard details when the DMI sysfs files are not
  readable. It is executed **without** `sudo`.
- `lsb_release` only when `/etc/os-release` is missing.

### Windows requirements

WMI/PowerShell and `dxdiag` are part of Windows. jHardware uses WMI4Java and
jSensors, which load native libraries through JNA. On JDK 24+ JNA may print a
native-access warning; the `jar-with-dependencies` manifest enables native
access for standalone runs.

## Installation

Maven:

```xml
<dependency>
    <groupId>org.jhardware</groupId>
    <artifactId>jHardware</artifactId>
    <version>1.0.0</version>
</dependency>
```

Gradle:

```groovy
implementation 'org.jhardware:jHardware:1.0.0'
```

## Usage

```java
import org.jutils.jhardware.HardwareInfo;
import org.jutils.jhardware.model.ProcessorInfo;

ProcessorInfo cpu = HardwareInfo.getProcessorInfo();

System.out.println("Model: " + cpu.getModelName());
System.out.println("Physical cores: " + cpu.getNumCores());
System.out.println("Temperature (C): " + cpu.getTemperature());

for (String warning : cpu.getWarnings()) {
    System.out.println("Warning: " + warning);
}

cpu.getFullInfo().forEach((key, value) ->
        System.out.println(key + " = " + value));
```

All components follow the same pattern:

```java
HardwareInfo.getMemoryInfo();
HardwareInfo.getBiosInfo();
HardwareInfo.getMotherboardInfo();
HardwareInfo.getOSInfo();
HardwareInfo.getNetworkInfo();
HardwareInfo.getDisplayInfo();
HardwareInfo.getGraphicsCardInfo();
```

### Typed data and raw data

- Typed getters are `null` when the platform cannot provide a value (or an empty
  list/array for collections). They never throw for missing data.
- `getFullInfo()` returns an unmodifiable, insertion-ordered map with every raw
  key/value pair retrieved for the component.
- `getWarnings()` returns an unmodifiable list explaining non-fatal omissions.
- Memory exposes both the platform string (`getTotalMemory()`) and a normalized
  value in bytes (`getTotalMemoryBytes()`).
- Temperatures are Celsius strings, or `NOT DETECTED`.
- Display modes use `WIDTHxHEIGHT@REFRESH` (for example `1920x1080@60.00`).
- Network types are `ethernet`, `wireless`, `bridge`, `loopback`,
  `point-to-point` or `virtual`.
- Every component call performs a fresh read; the only cache is the
  `DirectXInfoLoader` dxdiag report. On Windows,
  network snapshots include a warning because the JDK does not expose
  per-interface traffic counters (the old global `netstat` totals were
  misleading and were removed).

### Injecting an OS family and command runner

`HardwareInfoService` is the configurable entry point used by tests and tools
that need deterministic behavior:

```java
import java.time.Duration;
import org.jutils.jhardware.HardwareInfoService;
import org.jutils.jhardware.util.OsFamily;
import org.jutils.jhardware.util.SystemCommandRunner;

HardwareInfoService service = new HardwareInfoService(
        OsFamily.LINUX,
        new SystemCommandRunner(Duration.ofSeconds(5)));

var memory = service.getMemoryInfo();
```

`CommandRunner` is a single-method interface, so tests can supply canned
`CommandResult` values without spawning processes.

## Error handling

- An unsupported OS (macOS or an unknown family) throws
  `UnsupportedOperatingSystemException` with an actionable message.
- A failing or missing optional command is reported in `getWarnings()`; the
  corresponding typed value is `null` and lists are empty.
- A command that exceeds the timeout (10 seconds by default) is killed; the
  result is marked as timed out and the component degrades gracefully.
- jHardware never writes to `java.util.logging`, SLF4J or stdout on its own.
  The `main` method and the `jar-with-dependencies` are the only CLI-style
  exceptions.

## Building and testing

```bash
mvn verify
```

This compiles with Java 8 source and target levels, runs the JUnit 5 suite, and attaches the
source and Javadoc jars.

```bash
# Executable jar with every runtime dependency
mvn -Pwith-dependencies package
java -jar target/jHardware-1.0.0-jar-with-dependencies.jar

# Coverage report (JaCoCo)
mvn -Pcoverage verify
```

The suite contains 110 tests: parser fixtures for Linux and Windows, serialization
tests, command-runner timeout tests and guarded Linux integration tests. Windows
parser behavior is covered without needing a Windows runner; a Windows CI job
runs the platform-independent tests. The suite is also executed on a real
Temurin 8 runtime, and the published classes use Java 8 bytecode (major version
52).

## Similar projects

- [OSHI](https://github.com/oshi/oshi) uses JNA and provides broader native
  coverage. jHardware deliberately stays close to the JDK, `/proc`, `/sys`,
  WMI and well-known system tools.

## License

Apache License 2.0. See [LICENSE](LICENSE).
