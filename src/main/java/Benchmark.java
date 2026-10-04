import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Random;

public class Benchmark {
    private static final int WARMUP_RUNS = 2;
    private static final int MEASURED_RUNS = 5;


    private static volatile long sink;

    private static class Result {
        long time;
        long steps;
        long moves;
        long comparisons;

        Result(long time, Metrics metrics) {
            this.time = time;
            steps = metrics.getSteps();
            moves = metrics.getMoves();
            comparisons = metrics.getComparisons();
        }
    }

    public static void main(String[] args) throws IOException {
        Path directory = Path.of("results");
        Files.createDirectories(directory);

        Path output = directory.resolve("results.csv");

        try (PrintWriter writer = new PrintWriter(
                Files.newBufferedWriter(output))) {

            writer.println(
                    "workload,variant,structure,n,time_ms,"
                            + "steps,moves,comparisons"
            );

            int[] sizes = {100, 1000, 10000, 100000};
            String[] structures = {"DynamicArray", "MyLinkedList"};

            for (int n : sizes) {
                for (String structure : structures) {
                    saveCase(writer, "W1", "-", structure, n);
                    saveCase(writer, "W2", "-", structure, n);
                    saveCase(writer, "W3", "head", structure, n);
                    saveCase(writer, "W3", "middle", structure, n);
                }

                saveCase(writer, "W4", "-", "MinHeap", n);
            }
        }

        System.out.println("Finished!");
        System.out.println("CSV: " + output.toAbsolutePath());
    }

    private static void saveCase(
            PrintWriter writer,
            String workload,
            String variant,
            String structure,
            int n) {


        for (int i = 0; i < WARMUP_RUNS; i++) {
            runCase(workload, variant, structure, n);
        }

        Result[] results = new Result[MEASURED_RUNS];

        for (int i = 0; i < MEASURED_RUNS; i++) {
            results[i] = runCase(workload, variant, structure, n);
        }


        for (int i = 1; i < results.length; i++) {
            Result current = results[i];
            int j = i - 1;

            while (j >= 0 && results[j].time > current.time) {
                results[j + 1] = results[j];
                j--;
            }

            results[j + 1] = current;
        }


        Result median = results[MEASURED_RUNS / 2];
        double milliseconds = median.time / 1_000_000.0;

        writer.printf(
                Locale.US,
                "%s,%s,%s,%d,%.6f,%d,%d,%d%n",
                workload,
                variant,
                structure,
                n,
                milliseconds,
                median.steps,
                median.moves,
                median.comparisons
        );

        writer.flush();

        System.out.printf(
                Locale.US,
                "%s %s %s n=%d: %.3f ms%n",
                workload, variant, structure, n, milliseconds
        );
    }

    private static Result runCase(
            String workload,
            String variant,
            String structure,
            int n) {


        Random random = new Random(42);
        int[] values = new int[n];

        for (int i = 0; i < n; i++) {

            values[i] = random.nextInt(1_000_000);
        }

        if (workload.equals("W4")) {
            return runHeap(values);
        }

        IntList list = structure.equals("DynamicArray")
                ? new DynamicArray()
                : new MyLinkedList();


        for (int value : values) {
            list.add(value);
        }

        int queryCount = workload.equals("W1") ? 10000 : 1000;
        int[] queries = new int[queryCount];

        for (int i = 0; i < queryCount; i++) {
            if (workload.equals("W1")) {
                queries[i] = random.nextInt(n);
            } else if (workload.equals("W2")) {
                if (i % 2 == 0) {

                    queries[i] = values[random.nextInt(n)];
                } else {

                    queries[i] = -1 - random.nextInt(1_000_000);
                }
            } else {
                queries[i] = random.nextInt(1_000_000);
            }
        }

        Metrics metrics = list.getMetrics();
        metrics.reset();

        long checksum = 0;
        long start = System.nanoTime();

        if (workload.equals("W1")) {
            for (int index : queries) {
                checksum += list.get(index);
            }

        } else if (workload.equals("W2")) {
            for (int value : queries) {
                if (list.contains(value)) {
                    checksum++;
                }
            }

        } else {

            int index = variant.equals("head") ? 0 : n / 2;

            for (int value : queries) {
                list.add(index, value);
            }

            for (int i = 0; i < 1000; i++) {
                checksum += list.remove(index);
            }
        }

        long elapsed = System.nanoTime() - start;
        sink = checksum;


        if (workload.equals("W2") && checksum != 500) {
            throw new AssertionError("Incorrect search result");
        }

        if (list.size() != n) {
            throw new AssertionError("Incorrect final size");
        }

        return new Result(elapsed, metrics);
    }

    private static Result runHeap(int[] values) {
        MinHeap heap = new MinHeap();
        int[] sorted = new int[values.length];

        Metrics metrics = heap.getMetrics();
        metrics.reset();

        long start = System.nanoTime();


        for (int value : values) {
            heap.insert(value);
        }

        for (int i = 0; i < sorted.length; i++) {
            sorted[i] = heap.extractMin();
        }

        long elapsed = System.nanoTime() - start;


        long checksum = 0;

        for (int i = 0; i < sorted.length; i++) {
            if (i > 0 && sorted[i - 1] > sorted[i]) {
                throw new AssertionError("Output is not sorted");
            }

            checksum += sorted[i];
        }

        sink = checksum;

        if (heap.size() != 0) {
            throw new AssertionError("Heap is not empty");
        }

        return new Result(elapsed, metrics);
    }
}