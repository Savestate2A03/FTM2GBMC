class Note(val note: String, octave: Int, val instrument: Int, val volume: Int, val effects: Array<Effect?>) {
    val octave: Int
    var length: Int = 0

    init {
        var octave = octave
        if (octave != -1)
            octave++
        this.octave = octave
        length = -1
    }
}
