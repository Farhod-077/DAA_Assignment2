import org.junit.jupiter.api.Test;

import java.util.PriorityQueue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class MinHeapTest {

    @Test
    void emptyHeap() {
        MinHeap heap = new MinHeap();

        assertEquals(0, heap.size());
        assertTrue(heap.isValidHeap());

        assertThrows(IllegalStateException.class,
                () -> heap.peekMin());
        assertThrows(IllegalStateException.class,
                () -> heap.extractMin());
    }

    @Test
    void singleElementAndReuse() {
        MinHeap heap = new MinHeap();

        heap.insert(15);

        assertTrue(heap.isValidHeap());
        assertEquals(15, heap.peekMin());
        assertEquals(1, heap.size());
        assertEquals(15, heap.extractMin());

        assertEquals(0, heap.size());
        assertTrue(heap.isValidHeap());

        heap.insert(-5);

        assertEquals(-5, heap.extractMin());
        assertTrue(heap.isValidHeap());

        assertThrows(IllegalStateException.class,
                () -> heap.extractMin());
    }

    @Test
    void duplicatesAndNegativeValues() {
        MinHeap heap = new MinHeap();

        int[] input = {5, -2, 5, 0, -2};
        int[] expected = {-2, -2, 0, 5, 5};

        for (int value : input) {
            heap.insert(value);
            assertTrue(heap.isValidHeap());
        }

        for (int value : expected) {
            assertEquals(value, heap.extractMin());
            assertTrue(heap.isValidHeap());
        }

        assertEquals(0, heap.size());
    }

    @Test
    void randomMixedOperations() {
        MinHeap actual = new MinHeap();
        PriorityQueue<Integer> expected = new PriorityQueue<>();
        Random random = new Random(42);

        for (int i = 0; i < 3000; i++) {
            if (expected.isEmpty() || random.nextBoolean()) {
                int value = random.nextInt(2001) - 1000;

                actual.insert(value);
                expected.add(value);
            } else {
                int minimum = expected.remove();
                assertEquals(minimum, actual.extractMin());
            }

            assertEquals(expected.size(), actual.size());
            assertTrue(actual.isValidHeap());

            if (!expected.isEmpty()) {
                assertEquals(expected.peek().intValue(),
                        actual.peekMin());
            }
        }

        while (!expected.isEmpty()) {
            assertEquals(expected.remove().intValue(),
                    actual.extractMin());
            assertTrue(actual.isValidHeap());
        }

        assertEquals(0, actual.size());
    }

    @Test
    void sortedOutputAndResizing() {
        MinHeap heap = new MinHeap();
        Random random = new Random(42);

        for (int i = 0; i < 1000; i++) {
            heap.insert(random.nextInt());
            assertTrue(heap.isValidHeap());
        }

        int previous = heap.extractMin();
        assertTrue(heap.isValidHeap());

        for (int i = 1; i < 1000; i++) {
            int current = heap.extractMin();

            assertTrue(previous <= current);
            assertTrue(heap.isValidHeap());

            previous = current;
        }

        assertEquals(0, heap.size());
    }
    @Test
    void heapCounters() {
        MinHeap heap = new MinHeap();
        Metrics metrics = heap.getMetrics();

        heap.insert(10);


        metrics.reset();
        heap.insert(5);

        assertEquals(4, metrics.getSteps());
        assertEquals(2, metrics.getMoves());
        assertEquals(1, metrics.getComparisons());


        metrics.reset();
        heap.insert(20);

        assertEquals(2, metrics.getSteps());
        assertEquals(0, metrics.getMoves());
        assertEquals(1, metrics.getComparisons());


        metrics.reset();
        assertEquals(5, heap.peekMin());

        assertEquals(1, metrics.getSteps());
        assertEquals(0, metrics.getMoves());
        assertEquals(0, metrics.getComparisons());


        metrics.reset();
        assertEquals(5, heap.extractMin());

        assertEquals(6, metrics.getSteps());
        assertEquals(3, metrics.getMoves());
        assertEquals(1, metrics.getComparisons());


        assertTrue(heap.isValidHeap());

        assertEquals(6, metrics.getSteps());
        assertEquals(3, metrics.getMoves());
        assertEquals(1, metrics.getComparisons());

        assertEquals(2, heap.size());
        assertEquals(10, heap.peekMin());
    }
}