public class MyLinkedList implements IntList {
    private static class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    private Node head;
    private Node tail;
    private int size;
    private final Metrics metrics = new Metrics();

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "Index: " + index + ", size: " + size
            );
        }
    }

    private Node nodeAt(int index) {
        Node current = head;

        for (int i = 0; i < index; i++) {
            current = current.next;
            metrics.step();
        }

        return current;
    }

    @Override
    public void add(int value) {
        Node newNode = new Node(value);

        if (size == 0) {
            head = newNode;
            metrics.move();
        } else {
            tail.next = newNode;
            metrics.move();
        }

        tail = newNode;
        metrics.move();
        size++;
    }

    @Override
    public void add(int index, int value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(
                    "Index: " + index + ", size: " + size
            );
        }

        if (index == size) {
            add(value);
            return;
        }

        Node newNode = new Node(value);

        if (index == 0) {
            newNode.next = head;
            metrics.move();

            head = newNode;
            metrics.move();
        } else {
            Node previous = nodeAt(index - 1);

            newNode.next = previous.next;
            metrics.move();

            previous.next = newNode;
            metrics.move();
        }

        size++;
    }

    @Override
    public int remove(int index) {
        checkIndex(index);
        int removed;

        if (index == 0) {
            removed = head.value;

            head = head.next;
            metrics.step();
            metrics.move();

            if (size == 1) {
                tail = null;
                metrics.move();
            }
        } else {
            Node previous = nodeAt(index - 1);

            Node target = previous.next;
            metrics.step();

            removed = target.value;

            previous.next = target.next;
            metrics.step();
            metrics.move();

            if (index == size - 1) {
                tail = previous;
                metrics.move();
            }
        }

        size--;
        return removed;
    }

    @Override
    public int get(int index) {
        checkIndex(index);
        return nodeAt(index).value;
    }

    @Override
    public boolean contains(int value) {
        Node current = head;

        while (current != null) {
            metrics.compare();

            if (current.value == value) {
                return true;
            }

            current = current.next;
            metrics.step();
        }

        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Metrics getMetrics() {
        return metrics;
    }
}