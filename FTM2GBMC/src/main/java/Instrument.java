public class Instrument {

    private final int volume;
    private final int arp;
    private final int pitch;
    private final int duty;
    private final int ident;
    private final String name;

    private Instrument(int v, int a, int p, int d, int i, String n) {
        volume = v;
        arp = a;
        pitch = p;
        duty = d;
        ident = i;
        name = n;
    }

    public static Instrument instrumentBuilder(String macroLine) throws Exception {
        // INST2A03   7     6  -1   2  -1   3 "pitchy"
        // 0          1     2   3   4   5   6 7
        String[] split = macroLine.split("\\s+");
        if (!split[0].equals("INST2A03"))
            throw new Exception("Provided line was not a FamiTracker instrument!");
        int i = Integer.parseInt(split[1]);
        int v = Integer.parseInt(split[2]);
        int a = Integer.parseInt(split[3]);
        int p = Integer.parseInt(split[4]);
        int d = Integer.parseInt(split[6]);
        return new Instrument(v, a, p, d, i, split[7].replaceAll("\"", ""));
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

    public int getIdent() {
        return ident;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " --> " +
                "V:" + volume + ' ' +
                "A:" + arp + ' ' +
                "P:" + pitch + ' ' +
                "D:" + duty;
    }

}
