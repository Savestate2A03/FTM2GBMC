
import java.io.PrintWriter;
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
    
    private MacroVolume getVolumeMacroById(int num) {
        for (MacroVolume i : volumeMacros) {
            if (i.getIdent() == num)
                return i;
        }
        return null;
    }
    
    private MacroArp getArpMacroById(int num) {
        for (MacroArp i : arpeggioMacros) {
            if (i.getIdent() == num)
                return i;
        }
        return null;
    }
    
    private MacroDuty getDutyMacroById(int num) {
        for (MacroDuty i : dutyMacros) {
            if (i.getIdent() == num)
                return i;
        }
        return null;
    }
    
    private MacroPitch getPitchMacroById(int num) {
        for (MacroPitch i : pitchMacros) {
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
    
    public String build() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("; ============================\n");
        sb.append("; FILE GENERATED WITH FTM2GBMC\n");
        sb.append("; ============================\n");
        sb.append("; FTM2GBMC created by Savestate!\n\n");
        sb.append("; -- INFO --\n");
        sb.append("#TITLE \"").append(songTitle.replaceAll("\\\"", "''")).append("\"\n");
        sb.append("#AUTHOR \"").append(songAuthor.replaceAll("\\\"", "''")).append("\"\n");
        sb.append("#COPYRIGHT \"").append(songCopyright.replaceAll("\\\"", "''")).append("\"\n\n");
        sb.append("; -- WAVE Macros --\n");
        sb.append("#@0 {0123456789ABCDEFFEDCBA9876543210} ;Triangle Wave\n\n");
        sb.append("; -- Export Mode --\n");
        sb.append("; [0]gbs [1]bin [2]gbs+bin [3]gbdsp [4]dbdsp(dmg)\n");
        sb.append("#mode 0\n\n");
        sb.append("; -- Volume Macros --\n");
        sb.append(sb_MacroVolume()).append("\n");
        sb.append("#V127 {15,\\} ; Default Macro\n");
        sb.append("; -- Duty Cycle Macros --\n");
        sb.append(sb_MacroDuty()).append("\n");
        sb.append("#X127 {0,\\} ; Default Macro\n");
        sb.append("; -- Pitch Macros --\n");
        sb.append(sb_MacroPitch()).append("\n");
        sb.append("#F127 {0,\\} ; Default Macro\n");
        sb.append("; -- Tempo (temp unfinished) --\n");
        sb.append("'ABCD t128 T205\n\n");
        sb.append(sb_PulseChannel(0));
        sb.append("\n\n");
        sb.append(sb_PulseChannel(1));
        return sb.toString();
    }
   
    private StringBuilder sb_PulseChannel(int channel) throws Exception {
        ArrayList<Frame> frames = null;
        char chan = 0;
        StringBuilder sb = new StringBuilder();
        if (channel != 0 && channel != 1)
            throw new Exception("Provided channel (" + channel + ") doesn't exist!");
        switch (channel) {
            case 0:
                chan = 'A';
                frames = pulse1;
                break;
            case 1:
                chan = 'B';
                frames = pulse2;
                break;
        }
        sb.append('\'').append(chan).append(' ').append(" v15 ");
        //64th notes is the smallest unit of time
        boolean firstFrame = true;
        // First note is a blank note.
        Note previousNote = new Note("r", -1, -1, -1, new Effect[0]);
        // Go through all the orders
        for(Order o : orders) {
            int pulseFrame;
            if (channel == 0) 
                pulseFrame = o.getPulse1();
            else
                pulseFrame = o.getPulse2();
            // Get the frame, contains the note array
            Frame frame = getFrameById(pulseFrame, frames);
            // If we're not on the first frame, and there's a note buffer...
            // append the buffer to the previous note!
            if (frame.getBuffer() != 0 && !firstFrame)
                sb.append('^').append(getNoteLength(frame.getBuffer()));
            // if we're on the first frame, we'll start out with a rest
            // equal to the length of the buffer.
            if (firstFrame) {
                if (frame.getBuffer() != 0)
                    sb.append('r').append(getNoteLength(frame.getBuffer()));
            }
            // if we're not on the first frame and there's no buffer,
            // lets make a new line for sanity's sake.
            if (!firstFrame && frame.getBuffer() == 0)
                sb.append("\n").append('\'').append(chan).append(' ');
            // an iterator through all the notes...
            for (int i=0; i<frame.getNotes().size(); i++) {
                // get the current note
                Note n = frame.getNotes().get(i);
                // if the note has a volume set, push it to the output
                if (n.getVolume() != -1)
                    sb.append(" v").append(n.getVolume()).append(' ');
                // if there's an octave set (and it's not the same as the previous note) , push it to the output
                if (n.getOctave() != -1 && previousNote.getOctave() != n.getOctave())
                    sb.append(" o").append(n.getOctave()).append(' ');
                // if the current note's instrument is not blank and not equal to the 
                // previous note's instrument, we need to update the instruments!
                if (n.getInstrument() != -1 && previousNote.getInstrument() != n.getInstrument()) {
                    Instrument instrument = getInstrumentById(n.getInstrument());
                    MacroVolume volume = getVolumeMacroById(instrument.getVolume());
                    MacroPitch   pitch = getPitchMacroById(instrument.getPitch());
                    MacroDuty     duty = getDutyMacroById(instrument.getDuty());
                    // set the volume macro
                    if (volume != null)
                        sb.append(sb_volumeMML(volume));
                    else
                        sb.append(" zv127,0,0");
                    // set the pitch macro
                    if (pitch != null)
                        sb.append(sb_pitchMML(pitch));
                    else
                        sb.append(" zf127,0,0");
                    // set the duty macro
                    if (duty != null)
                        sb.append(sb_dutyMML(duty));
                    else
                        sb.append(" zw127,0,0");
                }
                // if the current note is empty (which means something else was set)
                // we slur it and set the length to the empty note.
                if (n.getNote().isEmpty()) {
                    sb.append(" & ");
                    sb.append(previousNote.getNote().toLowerCase());
                    sb.append(getNoteLength(n.getLength()));
                } else if (n.getNote().equals("---")) {
                    // if it's a note cut...
                    sb.append(" r").append(getNoteLength(n.getLength()));
                } else if (n.getNote().equals("===")) {
                    // if it's a note release
                    Instrument instrument = getInstrumentById(previousNote.getInstrument());
                    MacroVolume volume = getVolumeMacroById(instrument.getVolume() + 64);
                    MacroPitch   pitch = getPitchMacroById(instrument.getPitch() + 64);
                    MacroDuty     duty = getDutyMacroById(instrument.getDuty() + 64);
                    // set the volume macro
                    if (volume != null)
                        sb.append(sb_volumeMML(volume));
                    // set the pitch macro
                    if (pitch != null)
                        sb.append(sb_pitchMML(pitch));
                    // set the duty macro
                    if (duty != null)
                        sb.append(sb_dutyMML(duty));
                    // and send the note to the output.
                    sb.append(' ');
                    sb.append(previousNote.getNote().toLowerCase()).append(getNoteLength(n.getLength()));
                } else {
                    // otherwise we'll output the note like normal
                    sb.append(' ');
                    sb.append(n.getNote().toLowerCase()).append(getNoteLength(n.getLength()));
                }
                // if the note is not a cut or note-off, then we will
                // set it equal to the previous note.
                if (!(n.getNote().equals("===") || n.getNote().equals("---") || n.getNote().isEmpty()))
                    previousNote = n;
            }
            firstFrame = false;
        }
        return sb;
    }
    
    private StringBuilder sb_volumeMML(MacroVolume v) {
        StringBuilder sb = new StringBuilder();
        sb.append(" zv");
        sb.append(v.getIdent());
        sb.append(",1,0");
        return sb;
    }
    
    private StringBuilder sb_pitchMML(MacroPitch p) {
        StringBuilder sb = new StringBuilder();
        sb.append(" zf");
        sb.append(p.getIdent());
        sb.append(",1,0");
        return sb;
    }
    
    private StringBuilder sb_dutyMML(MacroDuty d) {
        StringBuilder sb = new StringBuilder();
        sb.append(" zw");
        sb.append(d.getIdent());
        sb.append(",1,0");
        return sb;
    }
    
    private String getNoteLength(int length) {
        String s = "";
        for (int i=0; i<length; i++) {
            s = s + "64^";
        }
        s = s.replaceAll("64\\^64\\^", "32^");
        s = s.replaceAll("32\\^32\\^", "16^");
        s = s.replaceAll("16\\^16\\^", "8^");
        s = s.replaceAll("8\\^8\\^", "4^");
        s = s.replaceAll("4\\^4\\^", "2^");
        s = s.replaceAll("2\\^2\\^", "1^");
        s = s.substring(0, s.length()-1);
        return s;
    }
    
    private StringBuilder sb_MacroVolume() {
        StringBuilder sb = new StringBuilder();
        //#V0 {0, -1, -2, -3, [-4, -5, -6] 2,] 2}
        for (MacroVolume v : volumeMacros) {
            int num = v.getIdent();
            sb.append("#V").append(num).append(" {");
            int loopPoint = v.getValues().length;
            if (v.getRelease() != -1)
                loopPoint = v.getRelease();
            for (int i=0; i<loopPoint; i++) {
                if (i == v.getLoop())
                    sb.append("[,");
                int value = v.getValues()[i];
                sb.append(value - 15);
                if (i < loopPoint-1) {
                    sb.append(", ");
                } else if (v.getLoop() != -1)
                    sb.append(",] 2,] 2}\n");
                else
                    sb.append(",\\}\n");
            }
            if (v.getRelease() != -1) {
                sb.append("#V").append(num+64).append(" {");
                for (int i=v.getRelease(); i<v.getValues().length; i++) {
                    if (i == v.getLoop())
                        sb.append("[,");
                    int value = v.getValues()[i];
                    sb.append(value - 15);
                    if (i < v.getValues().length-1) {
                        sb.append(", ");
                    } else if (v.getLoop() != -1)
                        sb.append(",] 2,] 2}");
                    else
                    sb.append(",\\}\n");
                }
                sb.append(" ; Release of ").append(num).append("\n");
            }
        }
        return sb;
    }

    private StringBuilder sb_MacroDuty() {
        StringBuilder sb = new StringBuilder();
        //#V0 {0, -1, -2, -3, [-4, -5, -6] 2,] 2}
        for (MacroDuty d : dutyMacros) {
            int num = d.getIdent();
            sb.append("#X").append(num).append(" {");
            int loopPoint = d.getValues().length;
            if (d.getRelease() != -1)
                loopPoint = d.getRelease();
            for (int i=0; i<loopPoint; i++) {
                if (i == d.getLoop())
                    sb.append("[,");
                int value = d.getValues()[i];
                sb.append(value);
                if (i < loopPoint-1) {
                    sb.append(", ");
                } else if (d.getLoop() != -1)
                    sb.append(",] 2,] 2}\n");
                else
                    sb.append(",\\}\n");
            }
            if (d.getRelease() != -1) {
                sb.append("#X").append(num+64).append(" {");
                for (int i=d.getRelease(); i<d.getValues().length; i++) {
                    if (i == d.getLoop())
                        sb.append('[');
                    int value = d.getValues()[i];
                    sb.append(value);
                    if (i < d.getValues().length-1) {
                        sb.append(", ");
                    } else if (d.getLoop() != -1)
                        sb.append(",] 2,] 2}");
                    else
                    sb.append(",\\}\n");
                }
                sb.append(" ; Release of ").append(num).append("\n");
            }
        }
        return sb;
    }

    private StringBuilder sb_MacroPitch() {
        StringBuilder sb = new StringBuilder();
        //#V0 {0, -1, -2, -3, [-4, -5, -6] 2,] 2}
        for (MacroPitch p : pitchMacros) {
            int num = p.getIdent();
            sb.append("#F").append(num).append(" {");
            int loopPoint = p.getValues().length;
            if (p.getRelease() != -1)
                loopPoint = p.getRelease();
            for (int i=0; i<loopPoint; i++) {
                if (i == p.getLoop())
                    sb.append("[,");
                int value = p.getValues()[i];
                sb.append(value);
                if (i < loopPoint-1) {
                    sb.append(", ");
                } else if (p.getLoop() != -1)
                    sb.append(",] 2,] 2}\n");
                else
                    sb.append(",\\}\n");
            }
            if (p.getRelease() != -1) {
                sb.append("#F").append(num+64).append(" {");
                for (int i=p.getRelease(); i<p.getValues().length; i++) {
                    if (i == p.getLoop())
                        sb.append('[');
                    int value = p.getValues()[i];
                    sb.append(value);
                    if (i < p.getValues().length-1) {
                        sb.append(", ");
                    } else if (p.getLoop() != -1)
                        sb.append("] 2,] 2}");
                    else
                    sb.append(",\\}\n");
                }
                sb.append(" ; Release of ").append(num).append("\n");
            }
        }
        return sb;
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("------------------------");
        System.out.println("|      Welcome to      |");
        System.out.println("| Savestate's FTM2GBMC |");
        System.out.println("------------------------");
        System.out.print("[Open] FamiTracker text export --> ");
        String input = sc.nextLine();
        input = input.replaceAll("\\\"", "");
        Charset encoding = Charset.defaultCharset();
        ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(Paths.get(input), encoding);
        FTM2GBMC ftm2gbmc = new FTM2GBMC(lines);
        System.out.print("[Save] GBMC .mml filename --> ");
        input = sc.nextLine();
        String output = ftm2gbmc.build();
        PrintWriter out = new PrintWriter(input);
        out.print(output);
        out.close();
    }
    
}
