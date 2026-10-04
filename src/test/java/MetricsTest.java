import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MetricsTest {

    @Test
    void countersStartAtZero() {
        Metrics metrics = new Metrics();

        assertEquals(0, metrics.getSteps());
        assertEquals(0, metrics.getMoves());
        assertEquals(0, metrics.getComparisons());
    }

    @Test
    void countersAreIndependent() {
        Metrics metrics = new Metrics();

        metrics.step();
        metrics.step();
        metrics.move();
        metrics.compare();
        metrics.compare();
        metrics.compare();

        assertEquals(2, metrics.getSteps());
        assertEquals(1, metrics.getMoves());
        assertEquals(3, metrics.getComparisons());
    }

    @Test
    void resetClearsAllCounters() {
        Metrics metrics = new Metrics();

        metrics.step();
        metrics.move();
        metrics.compare();

        metrics.reset();

        assertEquals(0, metrics.getSteps());
        assertEquals(0, metrics.getMoves());
        assertEquals(0, metrics.getComparisons());

        // Reset-тен кейін қайта санау жұмыс істеуі керек.
        metrics.step();

        assertEquals(1, metrics.getSteps());
        assertEquals(0, metrics.getMoves());
        assertEquals(0, metrics.getComparisons());
    }
}