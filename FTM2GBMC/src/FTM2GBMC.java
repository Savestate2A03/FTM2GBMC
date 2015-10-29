
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;


public class FTM2GBMC {

    private final ArrayList<String> text;
    
    // Song information
    private String songTitle;
    private String songAuthor;
    private String songCopyright;
    private int songSpeed;
    private int songTempo;
    
    // Macros
    ArrayList<MacroVolume> volumeMacros;
    ArrayList<MacroArp> arpeggioMacros;
    ArrayList<MacroPitch> pitchMacros;
    ArrayList<MacroDuty> dutyMacros;
    
    // Instruments
    ArrayList<Instrument> instruments;
    
    // Channels
    ArrayList<Frame> pulse1;
    ArrayList<Frame> pulse2;
    ArrayList<Frame> triangle;
    ArrayList<Frame> noise;
    
    // List of Frames
    ArrayList<Order> orders;
    
    public FTM2GBMC(ArrayList<String> textImport) throws Exception {
        this.text = textImport;
        init();
        information();
        buildOrders();
        buildFrames();
        buildMacros();
        buildInstruments();
    }
    
    private void init() {
        volumeMacros = new ArrayList<>();
        arpeggioMacros = new ArrayList<>();
        pitchMacros = new ArrayList<>();
        dutyMacros = new ArrayList<>();
        instruments = new ArrayList<>();
        pulse1 = new ArrayList<>();
        pulse2 = new ArrayList<>();
        triangle = new ArrayList<>();
        noise = new ArrayList<>();
        orders = new ArrayList<>();
    }
    
    private void buildMacros() throws Exception {
        int index = findText("MACRO");
        if (index == -1) {
            System.out.println("No macros found!");
            return;
        }
        System.out.println("Macros found on line " + index);
        System.out.println("Building macro list...");
        while(!text.get(index).isEmpty()) {
            String line = text.get(index);
            int macro = Integer.parseInt(text.get(index).split("\\s+")[1]);
            switch (macro) {
                case 0: 
                    volumeMacros.add(MacroVolume.volumeMacroBuilder(line));
                    break;
                case 1:
                    arpeggioMacros.add(MacroArp.arpMacroBuilder(line));
                    break;
                case 2:
                    pitchMacros.add(MacroPitch.pitchMacroBuilder(line));
                    break;
                case 4:
                    dutyMacros.add(MacroDuty.dutyMacroBuilder(line));
                    break;
            }
            index++;
        }
        System.out.println("...built " + (
                volumeMacros.size() +
                arpeggioMacros.size() +
                pitchMacros.size() + 
                dutyMacros.size()
                ) + " macros!");
        System.out.println(" Volume Macros:   " + volumeMacros.size());
        System.out.println(" Arpeggio Macros: " + arpeggioMacros.size());
        System.out.println(" Pitch Macros:    " + pitchMacros.size());
        System.out.println(" Duty Macros:     " + dutyMacros.size());
    }
    
    private void buildInstruments() throws Exception {
        int index = findText("INST2A03");
        if (index == -1)
            throw new Exception("No instruments found!");
        System.out.println("Instruments found on line " + index);
        System.out.println("Building instruments...");
        while(!text.get(index).isEmpty()) {
            String text = this.text.get(index);
            if (!text.startsWith("INST2A03")) {
                index++;
                continue;
            }
            instruments.add(Instrument.instrumentBuilder(text));
            index++;
        }
        System.out.println("...built " + instruments.size() + " instruments!");
    }
    
    private boolean doesFrameExist(int num, ArrayList<Frame> frames) {
        for (Frame f : frames) {
            if (f.getIdentity() == num)
                return true;
        }
        return false;
    }
    
    private Frame getFrameById(int num, ArrayList<Frame> frames) {
        for (Frame f : frames) {
            if (f.getIdentity() == num)
                return f;
        }
        return null;
    }
    
    private Instrument getInstrumentById(int num) {
        for (Instrument i : instruments) {
            if (i.getIdent() == num)
                return i;
        }
        return null;
    }
    
    private void buildFrames() throws Exception {
        System.out.println("Building frame list...");
        for (Order o : orders) {
            // Pulse 1 frames
            if (!doesFrameExist(o.getPulse1(), pulse1))
                pulse1.add(Frame.frameBuilder(0, o.getPulse1(), text));
            if (!doesFrameExist(o.getPulse2(), pulse2))
                pulse2.add(Frame.frameBuilder(1, o.getPulse2(), text));
            if (!doesFrameExist(o.getTriangle(), triangle))
                triangle.add(Frame.frameBuilder(2, o.getTriangle(), text));
            if (!doesFrameExist(o.getNoise(), noise))
                noise.add(Frame.frameBuilder(3, o.getNoise(), text));
        }
        System.out.println("...built " + (
                pulse1.size() + pulse2.size() + triangle.size() + noise.size()
                ) + " frames!");
        System.out.println(" Pulse 1 Frames:  " + pulse1.size());
        System.out.println(" Pulse 2 Frames:  " + pulse2.size());
        System.out.println(" Triangle Frames: " + triangle.size());
        System.out.println(" Noise Frames:    " + noise.size());
    }
    
    private void buildOrders() throws Exception {
        int ordersIndex = findText("ORDER");
        if (ordersIndex == -1)
            throw new Exception("No orders found!");
        System.out.println("Order list found on line " + ordersIndex);
        while(!text.get(ordersIndex).isEmpty()) {
            orders.add(Order.orderBuilder(text.get(ordersIndex)));
            ordersIndex++;
        }
        System.out.println("Total orders: " + orders.size());
    }
    
    private void information() {
        // Set the title, author, copyright, speed, and tempo
        songTitle = firstFoundLine("TITLE");
        songTitle = songTitle.split("\\s+", 2)[1];
        songTitle = songTitle.substring(1, songTitle.length()-1);
        songTitle = songTitle.replaceAll("\\\"\\\"", "\"");
        songAuthor = firstFoundLine("AUTHOR");
        songAuthor = songAuthor.split("\\s+")[1];
        songAuthor = songAuthor.substring(1, songAuthor.length()-1);
        songCopyright = firstFoundLine("COPYRIGHT");
        songCopyright = songCopyright.split("\\s+")[1];
        songCopyright = songCopyright.substring(1, songCopyright.length()-1);
        String trackInfo = firstFoundLine("TRACK");
        //TRACK 128   2 135 "New song"
        String[] info = trackInfo.split("\\s+");
        songSpeed = Integer.parseInt(info[2]);
        songTempo = Integer.parseInt(info[3]);
        System.out.println("Detected FTM Information:");
        System.out.println(" [Song]      " + songTitle);
        System.out.println(" [Author]    " + songAuthor);
        System.out.println(" [Copyright] " + songCopyright);
        System.out.println(" Speed: " + songSpeed);
        System.out.println(" Tempo: " + songTempo);
    }
    
    private int findText(String text) {
        for(int i=0; i<this.text.size(); i++) {
            if (this.text.get(i).startsWith(text))
                return i;
        }
        return -1;
    }
    
    private String firstFoundLine(String text) {
        int index = findText(text);
        if (index == -1)
            return "";
        return this.text.get(index);
    }
    
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("------------------------");
        System.out.println("|      Welcome to      |");
        System.out.println("| Savestate's FTM2GBMC |");
        System.out.println("------------------------");
        System.out.print("FamiTracker text export --> ");
        String input = sc.nextLine();
        input = input.replaceAll("\\\"", "");
        Charset encoding = Charset.defaultCharset();
        ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(Paths.get(input), encoding);
        FTM2GBMC ftm2gbmc = new FTM2GBMC(lines);
    }
    
}
