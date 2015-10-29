public class Order {
    
    private final int pulse1;
    private final int pulse2;
    private final int triangle;
    private final int noise;
    
    public Order(int pulse1, int pulse2, int triangle, int noise) {
        this.pulse1   = pulse1;
        this.pulse2   = pulse2;
        this.triangle = triangle;
        this.noise    = noise;
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
