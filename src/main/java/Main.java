public class Main {
    public static void main(String[] args) {
        MinHeap heap = new MinHeap();

        int[] values = {40, 10, 30, 5, 20, 5};

        for (int value : values) {
            heap.insert(value);

            if (!heap.isValidHeap()) {
                throw new AssertionError("Invalid heap after insert");
            }
        }

        System.out.println("Minimum: " + heap.peekMin());
        System.out.println("Size: " + heap.size());
        System.out.println("Sorted output:");

        while (heap.size() > 0) {
            System.out.println(heap.extractMin());

            if (!heap.isValidHeap()) {
                throw new AssertionError("Invalid heap after extract");
            }
        }

        System.out.println("Final size: " + heap.size());

        try {
            heap.peekMin();
        } catch (IllegalStateException e) {
            System.out.println("Empty heap: " + e.getMessage());
        }
    }
}