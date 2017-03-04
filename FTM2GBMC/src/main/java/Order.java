public class Order {

    private final int pulse1;
    private final int pulse2;
    private final int triangle;
    private final int noise;

    public Order(int pulse1, int pulse2, int triangle, int noise) {
        this.pulse1 = pulse1;
        this.pulse2 = pulse2;
        this.triangle = triangle;
        this.noise = noise;
    }

    public static Order orderBuilder(String line) throws Exception {
        // ORDER 0A : 01 05 03 02 02
        // 0     1  2 3  4  5  6  7
        String[] split = line.split("\\s+");
        if (!split[0].equals("ORDER"))
            throw new Exception("Provided line was not an Order!");
        int pulse1 = Integer.parseInt(split[3], 16);
        int pulse2 = Integer.parseInt(split[4], 16);
        int triangle = Integer.parseInt(split[5], 16);
        int noise = Integer.parseInt(split[6], 16);
        return new Order(pulse1, pulse2, triangle, noise);
    }

    public int getPulse1() {
        return pulse1;
    }

    public int getPulse2() {
        return pulse2;
    }

    public int getTriangle() {
        return triangle;
    }

    public int getNoise() {
        return noise;
    }
}
