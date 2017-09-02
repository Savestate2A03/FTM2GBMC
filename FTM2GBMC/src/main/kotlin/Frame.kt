import java.util.ArrayList

class Frame {

    var notes: ArrayList<Note>? = null
        private set
    var identity: Int = 0
        private set
    var buffer: Int = 0
        private set

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("Buffer: ").append(buffer).append("\n")
        for (n in notes!!) {
            val note = n.note
            if (note.isEmpty())
                sb.append("...")
            else
                sb.append(note)
            sb.append(' ')

            val o = n.octave
            if (o != -1)
                sb.append("O:").append(o).append(' ')

            val i = n.instrument
            if (i != -1)
                sb.append("I:").append(i).append(' ')

            val v = n.volume
            if (v != -1)
                sb.append("V:").append(v).append(' ')

            val effects = n.effects
            effects
                    .filterNotNull()
                    .forEach { sb.append("E:").append(it.type).append('|').append(it.getParam(0)).append('|').append(it.getParam(1)).append(' ') }
            sb.append("Length: ").append(n.length)
            sb.append("\n")
        }
        return sb.toString()
    }

    companion object {

        @Throws(Exception::class)
        fun frameBuilder(channel: Int, pattern: Int, lines: ArrayList<String>): Frame {
            val frame = Frame()
            frame.notes = ArrayList()
            frame.buffer = -1
            var hex = Integer.toHexString(pattern).toUpperCase()
            if (hex.length <= 1)
                hex = "0" + hex
            var index = lines.indices
                    .firstOrNull { lines[it].startsWith("PATTERN " + hex) }
                    ?.let { it + 1 }
                    ?: -1
            if (index == -1)
                throw Exception("Specified pattern $pattern ($hex) not found!")
            frame.identity = pattern
            var noteLength = 0
            val firstNote = true
            while (!lines[index].isEmpty()) {
                val channels = lines[index].trim { it <= ' ' }.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val info = channels[1 + channel].trim { it <= ' ' }.split("\\s+".toRegex(), 4).toTypedArray()
                var note = info[0]
                var instrument = -1
                var volume = -1
                var octave = -1
                try {
                    instrument = Integer.parseInt(info[1], 16)
                } catch (e: Exception) {
                }

                try {
                    volume = Integer.parseInt(info[2], 16)
                } catch (e: Exception) {
                }

                if (note == "...") {
                    note = ""
                } else if (note[2] == '#') {
                    note = note.substring(0, 2).replace("-".toRegex(), "")
                    note = note.replace("#".toRegex(), "+")
                } else if (note != "---" && note != "===") {
                    octave = Integer.parseInt(note.substring(2, 3))
                    note = note.substring(0, 2).replace("-".toRegex(), "")
                    note = note.replace("#".toRegex(), "+")
                }
                val stringEffects = info[3].split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val effects = Effect.effectsBuilder(stringEffects)
                if (!(note.isEmpty() && octave == -1 && volume == -1 && instrument == -1 && effects.isEmpty())) {
                    val builtNote = Note(note, octave, instrument, volume, effects)
                    if (!frame.notes!!.isEmpty()) {
                        frame.notes!![frame.notes!!.size - 1].length = noteLength
                    } else {
                        frame.buffer = noteLength
                    }
                    noteLength = 0
                    frame.notes!!.add(builtNote)
                }
                noteLength++
                index++
            }
            if (!frame.notes!!.isEmpty())
                frame.notes!![frame.notes!!.size - 1].length = noteLength
            else
                frame.buffer = noteLength

            return frame
        }
    }

}
