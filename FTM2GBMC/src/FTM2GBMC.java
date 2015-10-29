
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
    
    public FTM2GBMC(ArrayList<String> textImport) {
        this.textImport = textImport;
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
