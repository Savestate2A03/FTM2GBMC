class Note {

    private final String note;
    private final int octave;
    private final int instrument;
    private final int volume;
    private final Effect[] effects;
    private int length;

    public Note(String note, int octave, int instrument, int volume, Effect[] effects) {
        this.note = note;
        if (octave != -1)
            octave++;
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

    public void setLength(int length) {
        this.length = length;
    }

    public Effect[] getEffects() {
        return effects;
    }
}
