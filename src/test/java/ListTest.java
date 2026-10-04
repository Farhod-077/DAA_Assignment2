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
}