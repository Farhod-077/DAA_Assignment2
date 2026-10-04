import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class PlotGenerator {
    private static final int WIDTH = 1500;
    private static final int HEIGHT = 1000;

    private static final Color[] COLORS = {
            new Color(30, 100, 200),
            new Color(210, 70, 40),
            new Color(20, 145, 95),
            new Color(145, 65, 180)
    };

    private static class Row {
        String workload;
        String variant;
        String structure;
        int n;
        double time;
        long steps;
        long moves;
        long comparisons;

        Row(String line) {
            String[] cells = line.split(",");

            workload = cells[0];
            variant = cells[1];
            structure = cells[2];
            n = Integer.parseInt(cells[3]);
            time = Double.parseDouble(cells[4]);
            steps = Long.parseLong(cells[5]);
            moves = Long.parseLong(cells[6]);
            comparisons = Long.parseLong(cells[7]);
        }

        double metric(int index) {
            return switch (index) {
                case 0 -> time;
                case 1 -> steps;
                case 2 -> moves;
                default -> comparisons;
            };
        }
    }

    public static void main(String[] args) throws IOException {
        Path csv = Path.of("results", "results.csv");

        if (!Files.exists(csv)) {
            throw new IOException(
                    "results/results.csv not found. Run Benchmark first."
            );
        }

        Row[] rows = new Row[36];
        int count = 0;

        try (BufferedReader reader = Files.newBufferedReader(csv)) {
            reader.readLine(); // Тақырып жолын өткізу.

            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                if (count >= rows.length) {
                    throw new IOException("Too many CSV rows");
                }

                rows[count++] = new Row(line);
            }
        }

        if (count != 36) {
            throw new IOException(
                    "Expected 36 CSV rows, found: " + count
            );
        }

        Path output = Path.of("results", "plots");
        Files.createDirectories(output);

        for (int workload = 1; workload <= 4; workload++) {
            drawWorkload(rows, "W" + workload, output);
        }

        System.out.println("Plots saved: " + output.toAbsolutePath());
    }

    private static void drawWorkload(
            Row[] rows, String workload, Path output)
            throws IOException {

        String[] structures;
        String[] variants;
        String[] labels;

        if (workload.equals("W3")) {
            structures = new String[]{
                    "DynamicArray", "MyLinkedList",
                    "DynamicArray", "MyLinkedList"
            };

            variants = new String[]{
                    "head", "head", "middle", "middle"
            };

            labels = new String[]{
                    "DynamicArray head", "MyLinkedList head",
                    "DynamicArray middle", "MyLinkedList middle"
            };

        } else if (workload.equals("W4")) {
            structures = new String[]{"MinHeap"};
            variants = new String[]{"-"};
            labels = new String[]{"MinHeap"};

        } else {
            structures = new String[]{"DynamicArray", "MyLinkedList"};
            variants = new String[]{"-", "-"};
            labels = structures;
        }

        BufferedImage image = new BufferedImage(
                WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = image.createGraphics();

        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 26));
        g.drawString(workload + " benchmark results", 50, 40);

        g.setFont(new Font("SansSerif", Font.PLAIN, 17));

        for (int i = 0; i < labels.length; i++) {
            int x = 50 + i * 350;

            g.setColor(COLORS[i]);
            g.fillRect(x, 62, 22, 12);

            g.setColor(Color.BLACK);
            g.drawString(labels[i], x + 30, 75);
        }

        String[] titles = {
                "Median time (ms)",
                "Steps (count)",
                "Moves (count)",
                "Comparisons (count)"
        };

        for (int metric = 0; metric < 4; metric++) {
            int panelX = (metric % 2) * 750;
            int panelY = 100 + (metric / 2) * 440;

            drawPanel(
                    g, rows, workload, structures, variants,
                    metric, titles[metric], panelX, panelY
            );
        }

        g.dispose();

        Path file = output.resolve(workload + ".png");
        ImageIO.write(image, "png", file.toFile());

        System.out.println("Created: " + file);
    }

    private static void drawPanel(
            Graphics2D g,
            Row[] rows,
            String workload,
            String[] structures,
            String[] variants,
            int metric,
            String title,
            int panelX,
            int panelY) {

        int left = panelX + 115;
        int top = panelY + 55;
        int width = 575;
        int height = 285;

        double maximum = 0;

        for (Row row : rows) {
            if (row.workload.equals(workload)) {
                maximum = Math.max(maximum, row.metric(metric));
            }
        }

        double scale = maximum == 0 ? 1 : maximum * 1.1;

        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.drawString(title, left, panelY + 25);

        g.setFont(new Font("SansSerif", Font.PLAIN, 15));

        for (int tick = 0; tick <= 4; tick++) {
            int y = top + height - tick * height / 4;
            double value = scale * tick / 4;

            g.setColor(new Color(225, 225, 225));
            g.drawLine(left, y, left + width, y);

            g.setColor(Color.BLACK);
            g.drawString(format(value), panelX + 10, y + 5);
        }

        int[] sizes = {100, 1000, 10000, 100000};

        for (int i = 0; i < sizes.length; i++) {
            int x = left + i * width / 3;

            g.setColor(new Color(235, 235, 235));
            g.drawLine(x, top, x, top + height);

            g.setColor(Color.BLACK);
            g.drawString(
                    Integer.toString(sizes[i]),
                    x - 22,
                    top + height + 25
            );
        }

        g.setColor(Color.BLACK);
        g.drawLine(left, top, left, top + height);
        g.drawLine(left, top + height, left + width, top + height);

        g.drawString(
                "n (logarithmic scale)",
                left + 185,
                top + height + 55
        );

        for (int series = 0; series < structures.length; series++) {
            g.setColor(COLORS[series]);
            g.setStroke(new BasicStroke(2.5f));

            int previousX = 0;
            int previousY = 0;

            for (int i = 0; i < sizes.length; i++) {
                Row point = find(
                        rows, workload, structures[series],
                        variants[series], sizes[i]
                );

                int x = left + i * width / 3;
                int y = top + height
                        - (int) Math.round(
                        point.metric(metric) / scale * height
                );

                if (i > 0) {
                    g.drawLine(previousX, previousY, x, y);
                }

                g.fillOval(x - 4, y - 4, 8, 8);

                previousX = x;
                previousY = y;
            }
        }

        g.setStroke(new BasicStroke(1));

        if (maximum == 0) {
            g.setColor(Color.DARK_GRAY);
            g.drawString("All values are 0", left + 20, top + 25);
        }
    }

    private static Row find(
            Row[] rows, String workload,
            String structure, String variant, int n) {

        for (Row row : rows) {
            if (row.workload.equals(workload)
                    && row.structure.equals(structure)
                    && row.variant.equals(variant)
                    && row.n == n) {
                return row;
            }
        }

        throw new IllegalStateException("Missing CSV result");
    }

    private static String format(double value) {
        if (value == 0) {
            return "0";
        }

        if (value >= 10000 || value < 0.01) {
            return String.format(Locale.US, "%.1e", value);
        }

        return String.format(Locale.US, "%.2f", value);
    }
}