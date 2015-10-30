
import java.util.ArrayList;

public class Note {
    
    private final String note;
    private final int octave;
    private final int instrument;
    private final int volume;
    private int length;
    private final Effect[] effects;
    
    public Note(String note, int octave, int instrument, int volume, Effect[] effects) {
        this.note = note;
        if (octave != -1)
            octave += 2;
        this.octave = octave;
        this.instrument = instrument;
        this.volume = volume;
        length = -1;
        this.effects = effects;
    }
    
    public String getNote() {
        return note;
    }
    
    public int getOctave() {
        return octave;
    }
    
    public int getInstrument() {
        return instrument;
    }
    
    public int getVolume() {
        return volume;
    }
    
    public int getLength() {
        return length;
    }
    
    public Effect[] getEffects() {
        return effects;
    }
    
    public void setLength(int length) {
        this.length = length;
    }
}
