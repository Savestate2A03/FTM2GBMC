
import java.util.ArrayList;

public class Frame {
    
    private ArrayList<Note> notes;
    private int ident;
    private int noteStart;
    
    public static Frame frameBuilder(int channel, int pattern, ArrayList<String> lines) throws Exception {
        Frame frame = new Frame();
        frame.notes = new ArrayList<>();
        frame.noteStart = -1;
        int index = -1;
        String hex = Integer.toHexString(pattern);
        if (hex.length() <= 1)
            hex = "0" + hex;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith("PATTERN " + hex)) {
                index = i+1;
                break;
            }
        }
        if (index == -1)
            throw new Exception("Specified pattern not found!");
        frame.ident = pattern;
        int noteLength = 0;
        for(; !lines.get(index).isEmpty(); index++) {
            String[] channels = lines.get(index).trim().split(":");
            String[] info = channels[1+channel].trim().split("\\s+", 4);
            String note = info[0];
            int instrument = -1;
            int volume = -1;
            int octave = -1;
            try {
                instrument = Integer.parseInt(info[1], 16);
            } catch (Exception e) { }
            try {
                volume = Integer.parseInt(info[2], 16);
            } catch (Exception e) { }
            if (note.equals("...")) {
                note = "";
            } else if (!note.equals("---") && !note.equals("===")) {
                octave = Integer.parseInt(note.substring(2, 3));
                note = note.substring(0, 2).replaceAll("\\-", "");
                note = note.replaceAll("\\#", "+");
            }
            String[] stringEffects = info[3].split("\\s+");
            Effect[] effects = Effect.effectsBuilder(stringEffects);
            if (!(note.isEmpty() && (octave==-1) && (volume==-1) && (instrument==-1))) {
                Note builtNote = new Note(note, octave, instrument, volume, effects);
                if (!frame.notes.isEmpty()) {
                    frame.notes.get(frame.notes.size()-1).setLength(noteLength);
                    noteLength = 0;
                } else {
                    frame.noteStart = noteLength;
                }
                frame.notes.add(builtNote);
            }
            noteLength++;
        }
        if (!frame.notes.isEmpty())
            frame.notes.get(frame.notes.size()-1).setLength(noteLength);
        else 
            frame.noteStart = noteLength;
        
        return frame;
    }
    
    public int getIdentity() {
        return ident;
    }
    
    public ArrayList<Note> getNotes() {
        return notes;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Buffer: ").append(noteStart).append("\n");
        for(Note n : notes) {
            String note = n.getNote();
            if (note.isEmpty())
                sb.append("...");
            else 
                sb.append(note);
            sb.append(' ');
            
            int o = n.getOctave();
            if (o != -1)
                sb.append("O:").append(o).append(' ');
            
            int i = n.getInstrument();
            if (i != -1)
                sb.append("I:").append(i).append(' ');
            
            int v = n.getVolume();
            if (v != -1)
                sb.append("V:").append(v).append(' ');
            
            Effect[] effects = n.getEffects();
            for (Effect e : effects) {
                sb.append("E:").append(e.getType()).append('|').append(e.getParam(0)).append('|').append(e.getParam(1)).append(' ');
            }
            sb.append("Length: ").append(n.getLength());
            sb.append("\n");
        }
        return sb.toString();
    }
    
}
