public class Instrument {
    
    private final int volume;
    private final int arp;
    private final int pitch;
    private final int duty;
    String name;
    
    public Instrument(int v, int a, int p, int d, String n) {
        volume = v;
        arp    = a;
        pitch  = p;
        duty   = d;
        name   = n;
    }
    
    public int getVolume() {
        return volume;
    }
    
    public int getArp() {
        return arp;
    }
    
    public int getPitch() {
        return pitch;
    }
    
    public int getDuty() {
        return duty;
    }
    
    public String getName() {
        return name;
    }
    
    public static Instrument instrumentBuilder(String macroLine) throws Exception {
        // INST2A03   7     6  -1   2  -1   3 "pitchy"
        // 0          1     2   3   4   5   6 7
        String[] split = macroLine.split("\\s+");
        if (!split[0].equals("INST2A03"))
            throw new Exception("Provided line was not a FamiTracker instrument!");
        int v = Integer.parseInt(split[2]);
        int a = Integer.parseInt(split[3]);
        int p = Integer.parseInt(split[4]);
        int d = Integer.parseInt(split[6]);
        return new Instrument(v, a, p, d, split[7].replaceAll("\\\"", ""));
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" --> ");
        sb.append("V:").append(volume).append(' ');
        sb.append("A:").append(arp).append(' ');
        sb.append("P:").append(pitch).append(' ');
        sb.append("D:").append(duty);
        return sb.toString();
    }
    
}
