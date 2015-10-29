public class Note {
    
    private final String note;
    private final int octave;
    private final int instrument;
    private final int length;
    private Note next;
    
    public Note(String note, int octave, int instrument, int length) {
        next = null;
        this.note = note;
        this.octave = octave;
        this.instrument = instrument;
        this.length = length;
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
    
    public int getLength() {
        return length;
    }
    
    public Note next() {
        return next;
    }
    
    public void setNext(Note next) {
        this.next = next;
    }
}
