# bench
Scala benchmarking for JVM and JS.

# Statistics
Confidence Intervals via bootstrapping.

# Usage

```scala
"com.github.fdietze.bench" %%% "bench" % "8fd636f"
```

## ScalaJS

For accurate nodejs time measurements:
```scala
scalaJSModuleKind := ModuleKind.CommonJSModule
```

## Set your CPU to a fixed frequency
On Linux:
```bash
watch "lscpu | grep MHz"
```

```bash
sudo cpupower frequency-set -g performance
sudo cpupower frequency-set -u 3GHz
```

Reset (Often, powersave is the default):
```bash
sudo cpupower frequency-set -g powersave
```

## Pin thread to specific core?
