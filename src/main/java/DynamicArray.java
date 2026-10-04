public class DynamicArray implements IntList {
    private int[] data = new int[10];
    private int size = 0;
    private final Metrics metrics = new Metrics();

    private void ensureCapacity() {
        if (size < data.length) {
            return;
        }

        int[] newData = new int[data.length * 2];

        for (int i = 0; i < size; i++) {
            metrics.step();
            newData[i] = data[i];
            metrics.move();
        }

        data = newData;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "Index: " + index + ", size: " + size
            );
        }
    }

    @Override
    public void add(int value) {
        ensureCapacity();
        data[size] = value;
        size++;
    }

    @Override
    public void add(int index, int value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(
                    "Index: " + index + ", size: " + size
            );
        }

        ensureCapacity();

        for (int i = size; i > index; i--) {
            metrics.step();
            data[i] = data[i - 1];
            metrics.move();
        }

        data[index] = value;
        size++;
    }

    @Override
    public int remove(int index) {
        checkIndex(index);

        metrics.step();
        int removed = data[index];

        for (int i = index; i < size - 1; i++) {
            metrics.step();
            data[i] = data[i + 1];
            metrics.move();
        }

        size--;
        return removed;
    }

    @Override
    public int get(int index) {
        checkIndex(index);

        metrics.step();
        return data[index];
    }

    @Override
    public boolean contains(int value) {
        for (int i = 0; i < size; i++) {
            metrics.step();
            metrics.compare();

            if (data[i] == value) {
                return true;
            }
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