import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class ListTest {

    @Test
    void dynamicArrayEdgeCases() {
        checkEdgeCases(new DynamicArray());
    }

    @Test
    void linkedListEdgeCases() {
        checkEdgeCases(new MyLinkedList());
    }

    private void checkEdgeCases(IntList list) {

        assertEquals(0, list.size());
        assertFalse(list.contains(10));

        assertThrows(IndexOutOfBoundsException.class,
                () -> list.get(0));
        assertThrows(IndexOutOfBoundsException.class,
                () -> list.remove(0));
        assertThrows(IndexOutOfBoundsException.class,
                () -> list.add(-1, 10));
        assertThrows(IndexOutOfBoundsException.class,
                () -> list.add(1, 10));


        list.add(0, 10);
        assertEquals(10, list.get(0));
        assertEquals(10, list.remove(0));
        assertEquals(0, list.size());


        list.add(20);
        list.add(0, 5);
        list.add(list.size(), 30);

        assertEquals(5, list.get(0));
        assertEquals(30, list.get(list.size() - 1));


        list.add(20);
        assertTrue(list.contains(20));
        assertEquals(20, list.remove(1));
        assertTrue(list.contains(20));


        assertEquals(5, list.remove(0));
        assertEquals(20, list.remove(list.size() - 1));
        assertEquals(30, list.get(0));


        assertThrows(IndexOutOfBoundsException.class,
                () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class,
                () -> list.get(list.size()));
        assertThrows(IndexOutOfBoundsException.class,
                () -> list.remove(-1));
        assertThrows(IndexOutOfBoundsException.class,
                () -> list.remove(list.size()));
        assertThrows(IndexOutOfBoundsException.class,
                () -> list.add(list.size() + 1, 50));
    }

    @Test
    void dynamicArrayRandomOperations() {
        checkRandomOperations(new DynamicArray());
    }

    @Test
    void linkedListRandomOperations() {
        checkRandomOperations(new MyLinkedList());
    }

    private void checkRandomOperations(IntList actual) {
        ArrayList<Integer> expected = new ArrayList<>();
        Random random = new Random(42);

        for (int operation = 0; operation < 2000; operation++) {
            int choice = random.nextInt(5);
            int value = random.nextInt(101) - 50;

            if (choice == 0) {
                actual.add(value);
                expected.add(value);

            } else if (choice == 1) {
                int index = random.nextInt(expected.size() + 1);

                actual.add(index, value);
                expected.add(index, value);

            } else if (choice == 2 && !expected.isEmpty()) {
                int index = random.nextInt(expected.size());

                int removed = expected.remove(index);
                assertEquals(removed, actual.remove(index));

            } else if (choice == 3 && !expected.isEmpty()) {
                int index = random.nextInt(expected.size());

                assertEquals(expected.get(index).intValue(),
                        actual.get(index));

            } else {
                assertEquals(expected.contains(value),
                        actual.contains(value));
            }

            assertEquals(expected.size(), actual.size());


            for (int i = 0; i < expected.size(); i++) {
                assertEquals(expected.get(i).intValue(),
                        actual.get(i));
            }
        }
    }

    @Test
    void dynamicArrayResizing() {
        DynamicArray array = new DynamicArray();


        for (int i = 0; i < 1000; i++) {
            array.add(i);
        }

        assertEquals(1000, array.size());

        for (int i = 0; i < 1000; i++) {
            assertEquals(i, array.get(i));
        }

        array.add(0, -1);
        array.add(500, -2);

        assertEquals(-1, array.get(0));
        assertEquals(-2, array.get(500));
        assertEquals(1002, array.size());
    }
    @Test
    void dynamicArrayCounters() {
        DynamicArray array = new DynamicArray();

        array.add(10);
        array.add(20);
        array.add(30);

        Metrics metrics = array.getMetrics();


        metrics.reset();
        assertEquals(20, array.get(1));

        assertEquals(1, metrics.getSteps());
        assertEquals(0, metrics.getMoves());
        assertEquals(0, metrics.getComparisons());


        metrics.reset();
        assertTrue(array.contains(20));

        assertEquals(2, metrics.getSteps());
        assertEquals(0, metrics.getMoves());
        assertEquals(2, metrics.getComparisons());


        metrics.reset();
        array.add(0, 5);

        assertEquals(3, metrics.getSteps());
        assertEquals(3, metrics.getMoves());
        assertEquals(0, metrics.getComparisons());


        metrics.reset();
        assertEquals(5, array.remove(0));

        assertEquals(4, metrics.getSteps());
        assertEquals(3, metrics.getMoves());
        assertEquals(0, metrics.getComparisons());


        for (int i = 0; i < 7; i++) {
            array.add(i);
        }

        metrics.reset();
        array.add(99);

        assertEquals(11, array.size());
        assertEquals(10, metrics.getSteps());
        assertEquals(10, metrics.getMoves());
        assertEquals(0, metrics.getComparisons());
    }
    @Test
    void linkedListCounters() {
        MyLinkedList list = new MyLinkedList();

        list.add(10);
        list.add(20);
        list.add(30);

        Metrics metrics = list.getMetrics();


        metrics.reset();
        assertEquals(30, list.get(2));

        assertEquals(2, metrics.getSteps());
        assertEquals(0, metrics.getMoves());
        assertEquals(0, metrics.getComparisons());


        metrics.reset();
        assertTrue(list.contains(20));

        assertEquals(1, metrics.getSteps());
        assertEquals(0, metrics.getMoves());
        assertEquals(2, metrics.getComparisons());


        metrics.reset();
        list.add(0, 5);

        assertEquals(0, metrics.getSteps());
        assertEquals(2, metrics.getMoves());
        assertEquals(0, metrics.getComparisons());


        metrics.reset();
        assertEquals(5, list.remove(0));

        assertEquals(1, metrics.getSteps());
        assertEquals(1, metrics.getMoves());
        assertEquals(0, metrics.getComparisons());


        metrics.reset();
        assertEquals(30, list.remove(2));

        assertEquals(3, metrics.getSteps());
        assertEquals(2, metrics.getMoves());
        assertEquals(0, metrics.getComparisons());


        metrics.reset();
        list.add(40);

        assertEquals(0, metrics.getSteps());
        assertEquals(2, metrics.getMoves());
        assertEquals(0, metrics.getComparisons());

        assertEquals(3, list.size());
        assertEquals(40, list.get(2));
    }
}