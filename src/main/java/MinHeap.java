public class MinHeap {
    private int[] data = new int[10];
    private int size;
    private final Metrics metrics = new Metrics();

    private int read(int index) {
        metrics.step();
        return data[index];
    }

    private void ensureCapacity() {
        if (size < data.length) {
            return;
        }

        int[] newData = new int[data.length * 2];

        for (int i = 0; i < size; i++) {
            newData[i] = read(i);
            metrics.move();
        }

        data = newData;
    }

    private void swap(int first, int second) {
        int firstValue = read(first);
        int secondValue = read(second);

        data[first] = secondValue;
        metrics.move();

        data[second] = firstValue;
        metrics.move();
    }

    public void insert(int value) {
        ensureCapacity();

        int index = size;
        data[index] = value;
        size++;


        while (index > 0) {
            int parent = (index - 1) / 2;

            int currentValue = read(index);
            int parentValue = read(parent);
            metrics.compare();

            if (parentValue <= currentValue) {
                break;
            }

            swap(index, parent);
            index = parent;
        }
    }

    public int peekMin() {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty");
        }

        return read(0);
    }

    public int extractMin() {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty");
        }

        int minimum = read(0);
        size--;

        if (size == 0) {
            return minimum;
        }


        data[0] = read(size);
        metrics.move();

        int index = 0;


        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;

            if (left >= size) {
                break;
            }

            int smallestChild = left;
            int childValue = read(left);

            if (right < size) {
                int rightValue = read(right);
                metrics.compare();

                if (rightValue < childValue) {
                    smallestChild = right;
                    childValue = rightValue;
                }
            }

            int currentValue = read(index);
            metrics.compare();

            if (currentValue <= childValue) {
                break;
            }

            swap(index, smallestChild);
            index = smallestChild;
        }

        return minimum;
    }

    public int size() {
        return size;
    }

    public Metrics getMetrics() {
        return metrics;
    }


    public boolean isValidHeap() {
        for (int child = 1; child < size; child++) {
            int parent = (child - 1) / 2;

            if (data[parent] > data[child]) {
                return false;
            }
        }

        return true;
    }
}