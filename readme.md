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

# Machine configuration

## Disable Hyperthreading in the BIOS

## Set your CPU to a fixed frequency
```bash
watch "lscpu | grep MHz"
```

On Linux:
```bash
sudo cpupower frequency-set -g performance
sudo cpupower frequency-set -u 3GHz
```

Reset (Often, powersave is the default):
```bash
sudo cpupower frequency-set -g powersave
```

## Dedicate specific cores to the benchmark
https://stackoverflow.com/questions/11111852/how-to-shield-a-cpu-from-the-linux-scheduler-prevent-it-scheduling-threads-onto

Set a create cpuset called `bench` on cpu 1 and move kernel threads away from it:
```bash
# cset bench --cpu 1
# cset bench --kthread on
```

Reset:
```bash
# cset bench --reset
```

Execute command in `bench`:
```bash
# cset bench --exec mycommand -- -arg1 -arg2
```



## JVM: Pin thread to specific core?

## Garbage collector

## invoke Garbage collector before running sample?
- https://stackoverflow.com/questions/27321997/how-to-request-the-garbage-collector-in-node-js-to-run
- https://stackoverflow.com/questions/1481178/how-to-force-garbage-collection-in-java

## jit configuration


## no batching

## nanoTime/performance.now vs TimeMillis

## no mean

## https://llvm.org/docs/Benchmarking.html
