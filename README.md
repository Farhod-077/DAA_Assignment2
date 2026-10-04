# DAA Assignment 2

Student: Farkhod Sapayev
Group: SE-2525

## Overview

Java implementations of three data structures:

- DynamicArray with capacity doubling.
- MyLinkedList with head and tail references.
- Array-based MinHeap.

Structures store primitive int values. Operation counters record
steps, moves and element comparisons inside the methods.

## Requirements

- JDK 21
- Maven 3.6.3 or newer

## Project Layout

- src/main/java/ — structures, metrics, benchmark and plot generator
- src/test/java/ — JUnit 5 tests
- results/results.csv — benchmark measurements
- results/plots/ — four PNG charts
- REPORT.md — complexity analysis, proofs and discussion

## Build and Test

From the project root:

```shell
mvn clean verify
```

This compiles the project and runs the JUnit 5 tests.

If the project includes a Maven wrapper, Windows users can use:

```shell
.\mvnw.cmd clean verify
```

## Run the Benchmark

```shell
java -cp target/classes Benchmark
```

The benchmark creates results/results.csv.

For each case, two warm-up runs are discarded and the median of
five measured runs is saved. Inputs use Random(42).

Sizes: 100, 1000, 10000 and 100000.

Workloads:

- W1: 10000 random indexed reads.
- W2: 1000 searches, half present and half absent.
- W3: 1000 insertions followed by 1000 removals at the head
  or at the fixed initial middle index n/2.
- W4: n heap insertions followed by n extractions.

Initial filling is excluded from W1–W3 timing.
Both insertion and extraction are included in W4 timing.

## Generate Plots

After running the benchmark:

```shell
java -cp target/classes PlotGenerator
```

Output:

- results/plots/W1.png
- results/plots/W2.png
- results/plots/W3.png
- results/plots/W4.png

## Run in IntelliJ IDEA

1. Open the project as a Maven project.
2. Select JDK 21 and reload Maven dependencies.
3. Run ListTest and MinHeapTest.
4. Run Benchmark.main().
5. Run PlotGenerator.main().

Use Run rather than Debug for benchmark measurements.

## Counter Definitions

- Steps: array-cell reads or following next-node references.
- Moves: relocation of existing array elements or updates
  to stored list references.
- Comparisons: comparisons between element values.

Counters exclude index checks, loop-condition comparisons and
initial placement of a new array value. Benchmark timing includes
counter overhead.

## Release

Repository: ADD_YOUR_GITHUB_LINK
Release branch: main
Release tag: v1.0