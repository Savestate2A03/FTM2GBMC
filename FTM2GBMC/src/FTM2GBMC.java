
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;


public class FTM2GBMC {

    private final ArrayList<String> textImport;
    
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
        this.textImport = textImport;
        init();
        information();
        buildOrders();
        buildFrames();
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
    
    private boolean doesFrameExist(int num, ArrayList<Frame> frames) {
        for (Frame f : frames) {
            if (f.getIdentity() == num)
                return true;
        }
        return false;
    }
    
    private void buildFrames() throws Exception {
        System.out.println("Building frame list...");
        for (Order o : orders) {
            // Pulse 1 frames
            if (!doesFrameExist(o.getPulse1(), pulse1))
                pulse1.add(Frame.frameBuilder(0, o.getPulse1(), textImport));
            if (!doesFrameExist(o.getPulse2(), pulse2))
                pulse2.add(Frame.frameBuilder(0, o.getPulse1(), textImport));
            if (!doesFrameExist(o.getPulse1(), triangle))
                triangle.add(Frame.frameBuilder(0, o.getTriangle(), textImport));
            if (!doesFrameExist(o.getPulse1(), noise))
                noise.add(Frame.frameBuilder(0, o.getNoise(), textImport));
        }
        System.out.println("...built " + (
                pulse1.size() + pulse2.size() + triangle.size() + noise.size()
                ) + " frames!");
        System.out.println("Pulse 1 Frames:  " + pulse1.size());
        System.out.println("Pulse 2 Frames:  " + pulse2.size());
        System.out.println("Triangle Frames: " + triangle.size());
        System.out.println("Noise Frames:    " + noise.size());
    }
    
    private void buildOrders() throws Exception {
        int ordersIndex = findText("ORDER");
        if (ordersIndex == -1)
            throw new Exception("No orders found!");
        System.out.println("Found order list on line " + ordersIndex);
        while(!textImport.get(ordersIndex).isEmpty()) {
            orders.add(Order.orderBuilder(textImport.get(ordersIndex)));
            ordersIndex++;
        }
        System.out.println("Total orders: " + orders.size());
    }
    
    private void information() {
        // Set the title, author, copyright, speed, and tempo
        songTitle = searchForLine("TITLE");
        songTitle = songTitle.split("\\s+", 2)[1];
        songTitle = songTitle.substring(1, songTitle.length()-1);
        songTitle = songTitle.replaceAll("\\\"\\\"", "\"");
        songAuthor = searchForLine("AUTHOR");
        songAuthor = songAuthor.split("\\s+")[1];
        songAuthor = songAuthor.substring(1, songAuthor.length()-1);
        songCopyright = searchForLine("COPYRIGHT");
        songCopyright = songCopyright.split("\\s+")[1];
        songCopyright = songCopyright.substring(1, songCopyright.length()-1);
        String trackInfo = searchForLine("TRACK");
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
        for(int i=0; i<textImport.size(); i++) {
            if (textImport.get(i).startsWith(text))
                return i;
        }
        return -1;
    }
    
    private String searchForLine(String text) {
        int index = findText(text);
        if (index == -1)
            return "";
        return textImport.get(index);
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
