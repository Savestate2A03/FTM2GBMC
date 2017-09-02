import java.io.PrintWriter
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.util.ArrayList
import java.util.Scanner


class FTM2GBMC @Throws(Exception::class)
constructor(private val text: ArrayList<String>) {
    // Macros
    private lateinit var volumeMacros: ArrayList<MacroVolume>
    private lateinit var arpeggioMacros: ArrayList<MacroArp>
    private lateinit var pitchMacros: ArrayList<MacroPitch>
    private lateinit var dutyMacros: ArrayList<MacroDuty>
    // Instruments
    private lateinit var instruments: ArrayList<Instrument>
    // Channels
    private lateinit var pulse1: ArrayList<Frame>
    private lateinit var pulse2: ArrayList<Frame>
    private lateinit var triangle: ArrayList<Frame>
    private lateinit var noise: ArrayList<Frame>
    // List of Frames
    private lateinit var orders: ArrayList<Order>
    // Song information
    private var songTitle: String? = null
    private var songAuthor: String? = null
    private var songCopyright: String? = null
    private var songSpeed: Int = 0
    private var songTempo: Int = 0
    private var songBPM: Int = 0
    private var gbmcTempo: Int = 0

    init {
        init()
        information()
        buildOrders()
        buildFrames()
        buildMacros()
        buildInstruments()
    }

    private fun init() {
        volumeMacros = ArrayList()
        arpeggioMacros = ArrayList()
        pitchMacros = ArrayList()
        dutyMacros = ArrayList()
        instruments = ArrayList()
        pulse1 = ArrayList()
        pulse2 = ArrayList()
        triangle = ArrayList()
        noise = ArrayList()
        orders = ArrayList()
    }

    @Throws(Exception::class)
    private fun buildMacros() {
        var index = findText("MACRO")
        if (index == -1) {
            println("No macros found!")
            return
        }
        println("Macros found on line " + index)
        println("Building macro list...")
        while (!text[index].isEmpty()) {
            val line = text[index]
            val macro = Integer.parseInt(text[index].split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])
            when (macro) {
                0 -> volumeMacros.add(MacroVolume.volumeMacroBuilder(line))
                1 -> arpeggioMacros.add(MacroArp.arpMacroBuilder(line))
                2 -> pitchMacros.add(MacroPitch.pitchMacroBuilder(line))
                4 -> dutyMacros.add(MacroDuty.dutyMacroBuilder(line))
            }
            index++
        }
        println("...built " + (volumeMacros.size +
                arpeggioMacros.size +
                pitchMacros.size +
                dutyMacros.size) + " macros!")
        println(" Volume Macros:   " + volumeMacros.size)
        println(" Arpeggio Macros: " + arpeggioMacros.size)
        println(" Pitch Macros:    " + pitchMacros.size)
        println(" Duty Macros:     " + dutyMacros.size)
    }

    @Throws(Exception::class)
    private fun buildInstruments() {
        var index = findText("INST2A03")
        if (index == -1)
            throw Exception("No instruments found!")
        println("Instruments found on line " + index)
        println("Building instruments...")
        while (!text[index].isEmpty()) {
            val text = this.text[index]
            if (!text.startsWith("INST2A03")) {
                index++
                continue
            }
            instruments.add(Instrument.instrumentBuilder(text))
            index++
        }
        println("...built " + instruments.size + " instruments!")
    }

    private fun doesFrameExist(num: Int, frames: ArrayList<Frame>): Boolean {
        return frames.any { it.identity == num }
    }

    private fun getFrameById(num: Int, frames: ArrayList<Frame>?): Frame? {
        return frames!!.firstOrNull { it.identity == num }
    }

    private fun getInstrumentById(num: Int): Instrument? {
        return instruments.firstOrNull { it.ident == num }
    }

    private fun getVolumeMacroById(num: Int): MacroVolume? {
        return volumeMacros.firstOrNull { it.ident == num }
    }

    private fun getArpMacroById(num: Int): MacroArp? {
        return arpeggioMacros.firstOrNull { it.ident == num }
    }

    private fun getDutyMacroById(num: Int): MacroDuty? {
        return dutyMacros.firstOrNull { it.ident == num }
    }

    private fun getPitchMacroById(num: Int): MacroPitch? {
        return pitchMacros.firstOrNull { it.ident == num }
    }

    @Throws(Exception::class)
    private fun buildFrames() {
        println("Building frame list...")
        for (o in orders) {
            // Pulse 1 frames
            if (!doesFrameExist(o.pulse1, pulse1))
                pulse1.add(Frame.frameBuilder(0, o.pulse1, text))
            if (!doesFrameExist(o.pulse2, pulse2))
                pulse2.add(Frame.frameBuilder(1, o.pulse2, text))
            if (!doesFrameExist(o.triangle, triangle))
                triangle.add(Frame.frameBuilder(2, o.triangle, text))
            if (!doesFrameExist(o.noise, noise))
                noise.add(Frame.frameBuilder(3, o.noise, text))
        }
        println("...built " + (pulse1.size + pulse2.size + triangle.size + noise.size) + " frames!")
        println(" Pulse 1 Frames:  " + pulse1.size)
        println(" Pulse 2 Frames:  " + pulse2.size)
        println(" Triangle Frames: " + triangle.size)
        println(" Noise Frames:    " + noise.size)
    }

    @Throws(Exception::class)
    private fun buildOrders() {
        var ordersIndex = findText("ORDER")
        if (ordersIndex == -1)
            throw Exception("No orders found!")
        println("Order list found on line " + ordersIndex)
        while (!text[ordersIndex].isEmpty()) {
            orders.add(Order.orderBuilder(text[ordersIndex]))
            ordersIndex++
        }
        println("Total orders: " + orders.size)
    }

    @Throws(Exception::class)
    private fun information() {
        // Set the title, author, copyright, speed, and tempo
        songTitle = firstFoundLine("TITLE")
        songTitle = songTitle!!.split("\\s+".toRegex(), 2).toTypedArray()[1]
        songTitle = songTitle!!.substring(1, songTitle!!.length - 1)
        songTitle = songTitle!!.replace("\"\"\\\\\\\"\"".toRegex(), "\"")
        songAuthor = firstFoundLine("AUTHOR")
        songAuthor = songAuthor!!.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        songAuthor = songAuthor!!.substring(1, songAuthor!!.length - 1)
        songCopyright = firstFoundLine("COPYRIGHT")
        songCopyright = songCopyright!!.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        songCopyright = songCopyright!!.substring(1, songCopyright!!.length - 1)
        val trackInfo = firstFoundLine("TRACK")
        //TRACK 128   2 135 "New song"
        val info = trackInfo.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        songSpeed = Integer.parseInt(info[2])
        songTempo = Integer.parseInt(info[3])
        songBPM = songTempo * 6 / songSpeed
        println("Detected FTM Information:")
        println(" [Song]      " + songTitle!!)
        println(" [Author]    " + songAuthor!!)
        println(" [Copyright] " + songCopyright!!)
        println(" Speed: " + songSpeed)
        println(" Tempo: " + songTempo)
        println(" BPM:   " + songBPM)
        songBPM /= 4
        gbmcTempo = 256 * (songBPM - 20) / songBPM
        if (gbmcTempo > 255 || gbmcTempo < 1)
            throw Exception("GBMC tempo out of range! (1-255) --> [$gbmcTempo]")
    }

    private fun findText(text: String): Int {
        return this.text.indices.firstOrNull { this.text[it].startsWith(text) }
                ?: -1
    }

    private fun firstFoundLine(text: String): String {
        val index = findText(text)
        return if (index == -1) "" else this.text[index]
    }

    @Throws(Exception::class)
    fun build(): String {
        val sb = StringBuilder()
        sb.append("; ============================\n")
        sb.append("; FILE GENERATED WITH FTM2GBMC\n")
        sb.append("; ============================\n")
        sb.append("; FTM2GBMC created by Savestate!\n\n")
        sb.append("; -- INFO --\n")
        sb.append("#TITLE \"").append(songTitle!!.replace("\\\"".toRegex(), "''")).append("\"\n")
        sb.append("#AUTHOR \"").append(songAuthor!!.replace("\\\"".toRegex(), "''")).append("\"\n")
        sb.append("#COPYRIGHT \"").append(songCopyright!!.replace("\\\"".toRegex(), "''")).append("\"\n\n")
        sb.append("; -- WAVE Macros --\n")
        sb.append("#@0 {0123456789ABCDEFFEDCBA9876543210} ;Triangle Wave\n\n")
        sb.append("; -- Export Mode --\n")
        sb.append("; [0]gbs [1]bin [2]gbs+bin [3]gbdsp [4]dbdsp(dmg)\n")
        sb.append("#mode 0\n\n")
        sb.append("; -- Volume Macros --\n")
        sb.append(sb_MacroVolume()).append("\n")
        sb.append("#V127 {15,\\} ; Default Macro\n")
        sb.append("; -- Duty Cycle Macros --\n")
        sb.append(sb_MacroDuty()).append("\n")
        sb.append("#X127 {0,\\} ; Default Macro\n")
        sb.append("; -- Pitch Macros --\n")
        sb.append(sb_MacroPitch()).append("\n")
        sb.append("#F127 {0,\\} ; Default Macro\n")
        sb.append("; -- Tempo --\n")
        sb.append("'ABCD t128 T").append(gbmcTempo).append("\n\n")
        sb.append(sb_PulseChannel(0))
        sb.append("\n\n")
        sb.append(sb_PulseChannel(1))
        sb.append("\n\n")
        sb.append(sb_TriangleChannel())
        sb.append("\n\n")
        sb.append(sb_NoiseChannel())
        return sb.toString()
    }

    @Throws(Exception::class)
    private fun sb_PulseChannel(channel: Int): StringBuilder {
        var frames: ArrayList<Frame>? = null
        var chan: Char = 0.toChar()
        val sb = StringBuilder()
        if (channel != 0 && channel != 1)
            throw Exception("Provided channel ($channel) doesn't exist!")
        when (channel) {
            0 -> {
                chan = 'A'
                frames = pulse1
            }
            1 -> {
                chan = 'B'
                frames = pulse2
            }
        }
        sb.append('\'').append(chan).append(' ').append(" v15 ")
        //64th notes is the smallest unit of time
        var firstFrame = true
        var prevDutyCycle = 0
        var resetDutyCycle = true
        // First note is a blank note.
        var previousNote = Note("r", -1, -1, -1, arrayOfNulls(0))
        // Go through all the orders
        for (o in orders) {
            val pulseFrame: Int = if (channel == 0)
                o.pulse1
            else
                o.pulse2
            // Get the frame, contains the note array
            val frame = getFrameById(pulseFrame, frames)
            var forceInstrumentCheck = false
            // If we're not on the first frame, and there's a note buffer...
            // append the buffer to the previous note!
            if (frame!!.buffer != 0 && !firstFrame)
                sb.append('^').append(getNoteLength(frame.buffer))
            // if we're on the first frame, we'll start out with a rest
            // equal to the length of the buffer.
            if (firstFrame) {
                if (frame.buffer != 0)
                    sb.append('r').append(getNoteLength(frame.buffer))
            }
            // if we're not on the first frame and there's no buffer,
            // lets make a new line for sanity's sake.
            if (!firstFrame && frame.buffer == 0)
                sb.append("\n").append('\'').append(chan).append(' ')
            // an iterator through all the notes...
            for (i in 0 until frame.notes!!.size) {
                // note slide stuff
                var slideAmount = 0
                // ---
                // get the current note
                val n = frame.notes!![i]
                // if the note has a volume set, push it to the output
                if (n.volume != -1)
                    sb.append(" v").append(n.volume).append(' ')
                // Effects
                if (n.effects.isNotEmpty()) {
                    // if there are effects...
                    loop@ for (e in n.effects) {
                        val effect = e!!.type[0]
                        when (effect) {
                            'A' -> {
                                sb.append(" k")
                                if (n.volume == -1) {
                                    if (previousNote.volume == -1)
                                        break@loop
                                    sb.append(previousNote.volume)
                                } else {
                                    sb.append(n.volume)
                                }
                                sb.append(",")
                                val direction = e.getParam(1) - e.getParam(0) > 0
                                if (direction)
                                    sb.append('0')
                                else
                                    sb.append('1')
                                sb.append(',')
                                var speed = Math.abs(e.getParam(1) - e.getParam(0))
                                if (speed != 0) {
                                    speed /= 2
                                    speed = 7 - speed
                                    if (speed == 0)
                                        speed = 1
                                }
                                sb.append(speed)
                            }
                            'V' -> {
                                forceInstrumentCheck = true
                                sb.append(" x")
                                sb.append(e.getParam(1))
                                prevDutyCycle = e.getParam(1)
                            }
                            'Q' -> slideAmount = e.getParam(1)
                            'R' -> slideAmount = -e.getParam(1)
                            'W' -> sb.append(" L ")
                            'P' -> {
                                var detuneAmount = e.getParam(0) * 16 + e.getParam(1)
                                detuneAmount -= 0x80
                                sb.append(" %").append(detuneAmount).append(' ')
                            }
                            'J' -> {
                                sb.append(" p")
                                if (e.getParam(0) == 0 && e.getParam(1) == 0) {
                                    sb.append("0")
                                } else if (e.getParam(0) != 0 && e.getParam(1) == 0) {
                                    sb.append("1")
                                } else if (e.getParam(0) == 0 && e.getParam(1) != 0) {
                                    sb.append("2")
                                } else {
                                    sb.append("3")
                                }
                            }
                            'H' -> {
                                sb.append(" @ v")
                                sb.append(e.getParam(0) / 2)
                                sb.append(",")
                                sb.append(e.getParam(1) / 2)
                            }
                        }
                    }
                }
                // if there's an octave set (and it's not the same as the previous note) , push it to the output
                if (n.octave != -1 && previousNote.octave != n.octave)
                    sb.append(" o").append(n.octave).append(' ')
                // if the current note's instrument is not blank and not equal to the
                // previous note's instrument, we need to update the instruments!
                if (forceInstrumentCheck && previousNote.instrument != -1) {
                    val instrument = getInstrumentById(previousNote.instrument)
                    val volume = getVolumeMacroById(instrument!!.volume)
                    val pitch = getPitchMacroById(instrument.pitch)
                    val duty = getDutyMacroById(instrument.duty)
                    // set the volume macro
                    if (volume != null)
                        sb.append(sb_volumeMML(volume))
                    // set the pitch macro
                    if (pitch != null)
                        sb.append(sb_pitchMML(pitch))
                    // set the duty macro
                    if (duty != null)
                        sb.append(sb_dutyMML(duty))
                }
                if (n.instrument != -1 && previousNote.instrument != n.instrument) {
                    val instrument = getInstrumentById(n.instrument)
                    val volume = getVolumeMacroById(instrument!!.volume)
                    val pitch = getPitchMacroById(instrument.pitch)
                    val duty = getDutyMacroById(instrument.duty)
                    // set the volume macro
                    if (volume != null)
                        sb.append(sb_volumeMML(volume))
                    else
                        sb.append(" zv127,0,0")
                    // set the pitch macro
                    if (pitch != null)
                        sb.append(sb_pitchMML(pitch))
                    else
                        sb.append(" zf127,0,0")
                    // set the duty macro
                    if (duty != null) {
                        sb.append(sb_dutyMML(duty))
                        resetDutyCycle = true
                    } else if (resetDutyCycle) {
                        sb.append(" zw127,0,0")
                        sb.append(" x").append(prevDutyCycle)
                        resetDutyCycle = false
                    }
                }
                // if the current note is empty (which means something else was set)
                // we slur it and set the length to the empty note.
                if (n.note.isEmpty()) {
                    sb.append(" & ")
                    if (slideAmount != 0) {
                        sb.append(" {")
                        sb.append(previousNote.note.toLowerCase())
                        sb.append(',')
                        var noteNum = noteToNum(previousNote.note.toLowerCase())
                        noteNum += slideAmount
                        val note = numToNote(noteNum)
                        sb.append(note)
                        sb.append('}')
                        sb.append(getNoteLength(n.length))
                    } else {
                        sb.append(previousNote.note.toLowerCase())
                        sb.append(getNoteLength(n.length))
                    }
                } else if (n.note == "---") {
                    // if it's a note cut...
                    sb.append(" r").append(getNoteLength(n.length))
                } else if (n.note == "===") {
                    // if it's a note release
                    val instrument = getInstrumentById(previousNote.instrument)
                    val volume = getVolumeMacroById(instrument!!.volume + 64)
                    val pitch = getPitchMacroById(instrument.pitch + 64)
                    val duty = getDutyMacroById(instrument.duty + 64)
                    // set the volume macro
                    if (volume != null)
                        sb.append(sb_volumeMML(volume))
                    // set the pitch macro
                    if (pitch != null)
                        sb.append(sb_pitchMML(pitch))
                    // set the duty macro
                    if (duty != null)
                        sb.append(sb_dutyMML(duty))
                    // and send the note to the output.
                    sb.append(' ')
                    sb.append(previousNote.note.toLowerCase()).append(getNoteLength(n.length))
                } else if (slideAmount != 0) {
                    sb.append(" {")
                    sb.append(n.note.toLowerCase())
                    sb.append(',')
                    var noteNum = noteToNum(n.note.toLowerCase())
                    noteNum += slideAmount
                    val note = numToNote(noteNum)
                    sb.append(note)
                    sb.append('}')
                    sb.append(getNoteLength(n.length))
                } else {
                    // otherwise we'll output the note like normal
                    sb.append(' ')
                    sb.append(n.note.toLowerCase()).append(getNoteLength(n.length))
                }
                // if the note is not a cut or note-off, then we will
                // set it equal to the previous note.
                if (!(n.note == "===" || n.note == "---" || n.note.isEmpty()))
                    previousNote = n
            }
            firstFrame = false
        }
        return sb
    }

    @Throws(Exception::class)
    private fun sb_TriangleChannel(): StringBuilder {
        val frames = triangle
        val sb = StringBuilder()
        val chan = 'C'
        sb.append('\'').append(chan).append(' ').append(" @0 v3 ")
        //64th notes is the smallest unit of time
        var firstFrame = true
        // First note is a blank note.
        var previousNote = Note("r", -1, -1, -1, arrayOfNulls(0))
        // Go through all the orders
        for (o in orders) {
            val triangleFrame: Int = o.triangle
            // Get the frame, contains the note array
            val frame = getFrameById(triangleFrame, frames)
            val forceInstrumentCheck = false
            // If we're not on the first frame, and there's a note buffer...
            // append the buffer to the previous note!
            if (frame!!.buffer != 0 && !firstFrame)
                sb.append('^').append(getNoteLength(frame.buffer))
            // if we're on the first frame, we'll start out with a rest
            // equal to the length of the buffer.
            if (firstFrame) {
                if (frame.buffer != 0)
                    sb.append('r').append(getNoteLength(frame.buffer))
            }
            // if we're not on the first frame and there's no buffer,
            // lets make a new line for sanity's sake.
            if (!firstFrame && frame.buffer == 0)
                sb.append("\n").append('\'').append(chan).append(' ')
            // an iterator through all the notes...
            for (i in 0 until frame.notes!!.size) {
                // note slide stuff
                var slideAmount = 0
                // ---
                // get the current note
                val n = frame.notes!![i]
                // Effects
                if (n.effects.isNotEmpty()) {
                    // if there are effects...
                    for (e in n.effects) {
                        val effect = e!!.type[0]
                        when (effect) {
                            'Q' -> slideAmount = e.getParam(1)
                            'R' -> slideAmount = -e.getParam(1)
                            'W' -> sb.append(" L ")
                            'P' -> {
                                var detuneAmount = e.getParam(0) * 16 + e.getParam(1)
                                detuneAmount -= 0x80
                                sb.append(" %").append(detuneAmount).append(' ')
                            }
                            'J' -> {
                                sb.append(" p")
                                if (e != null) {
                                    if (e.getParam(0) == 0 && e.getParam(1) == 0) {
                                        sb.append("0")
                                    } else if (e != null) {
                                        if (e.getParam(0) != 0 && e.getParam(1) == 0) {
                                            sb.append("1")
                                        } else if (e.getParam(0) == 0 && e.getParam(1) != 0) {
                                            sb.append("2")
                                        } else {
                                            sb.append("3")
                                        }
                                    }
                                }
                            }
                            'H' -> {
                                sb.append(" @ v")
                                sb.append(e.getParam(0) / 2)
                                sb.append(",")
                                sb.append(e.getParam(1) / 2)
                            }
                        }
                    }
                }
                // if the note has a volume set, push it to the output
                if (n.volume != -1)
                    sb.append(" v").append(n.volume / 5).append(' ')
                // if there's an octave set (and it's not the same as the previous note) , push it to the output
                if (n.octave != -1 && previousNote.octave != n.octave)
                    sb.append(" o").append(n.octave).append(' ')
                // if the current note's instrument is not blank and not equal to the
                // previous note's instrument, we need to update the instruments!
                if (forceInstrumentCheck && previousNote.instrument != -1) {
                    val instrument = getInstrumentById(previousNote.instrument)
                    val volume = getVolumeMacroById(instrument!!.volume)
                    val pitch = getPitchMacroById(instrument.pitch)
                    // set the volume macro
                    if (volume != null)
                        sb.append(sb_volumeMML(volume, true))
                    // set the pitch macro
                    if (pitch != null)
                        sb.append(sb_pitchMML(pitch))
                }
                if (n.instrument != -1 && previousNote.instrument != n.instrument) {
                    val instrument = getInstrumentById(n.instrument)
                    val volume = getVolumeMacroById(instrument!!.volume)
                    val pitch = getPitchMacroById(instrument.pitch)
                    // set the volume macro
                    if (volume != null)
                        sb.append(sb_volumeMML(volume, true))
                    else
                        sb.append(" zv127,0,0")
                    // set the pitch macro
                    if (pitch != null)
                        sb.append(sb_pitchMML(pitch))
                    else
                        sb.append(" zf127,0,0")
                }
                // if the current note is empty (which means something else was set)
                // we slur it and set the length to the empty note.
                if (n.note.isEmpty()) {
                    sb.append(" & ")
                    if (slideAmount != 0) {
                        sb.append(" {")
                        sb.append(previousNote.note.toLowerCase())
                        sb.append(',')
                        var noteNum = noteToNum(previousNote.note.toLowerCase())
                        noteNum += slideAmount
                        val note = numToNote(noteNum)
                        sb.append(note)
                        sb.append('}')
                        sb.append(getNoteLength(n.length))
                    } else {
                        sb.append(previousNote.note.toLowerCase())
                        sb.append(getNoteLength(n.length))
                    }
                } else if (n.note == "---") {
                    // if it's a note cut...
                    sb.append(" r").append(getNoteLength(n.length))
                } else if (n.note == "===") {
                    // if it's a note release
                    val instrument = getInstrumentById(previousNote.instrument)
                    val volume = getVolumeMacroById(instrument!!.volume + 64)
                    val pitch = getPitchMacroById(instrument.pitch + 64)
                    // set the volume macro
                    if (volume != null)
                        sb.append(sb_volumeMML(volume, true))
                    // set the pitch macro
                    if (pitch != null)
                        sb.append(sb_pitchMML(pitch))
                    // and send the note to the output.
                    sb.append(' ')
                    sb.append(previousNote.note.toLowerCase()).append(getNoteLength(n.length))
                } else if (slideAmount != 0) {
                    sb.append(" {")
                    sb.append(n.note.toLowerCase())
                    sb.append(',')
                    var noteNum = noteToNum(n.note.toLowerCase())
                    noteNum += slideAmount
                    val note = numToNote(noteNum)
                    sb.append(note)
                    sb.append('}')
                    sb.append(getNoteLength(n.length))
                } else {
                    // otherwise we'll output the note like normal
                    sb.append(' ')
                    sb.append(n.note.toLowerCase()).append(getNoteLength(n.length))
                }
                // if the note is not a cut or note-off, then we will
                // set it equal to the previous note.
                if (!(n.note == "===" || n.note == "---" || n.note.isEmpty()))
                    previousNote = n
            }
            firstFrame = false
        }
        return sb
    }

    @Throws(Exception::class)
    private fun sb_NoiseChannel(): StringBuilder {
        val frames = noise
        val chan = 'D'
        val sb = StringBuilder()
        sb.append('\'').append(chan).append(' ').append(" v15 ")
        //64th notes is the smallest unit of time
        var firstFrame = true
        val prevDutyCycle = 0
        val resetDutyCycle = true
        // First note is a blank note.
        var previousNote = Note("r", -1, -1, -1, arrayOfNulls(0))
        // Go through all the orders
        for (o in orders) {
            val noiseFrame = o.noise
            // Get the frame, contains the note array
            val frame = getFrameById(noiseFrame, frames)
            val forceInstrumentCheck = false
            // If we're not on the first frame, and there's a note buffer...
            // append the buffer to the previous note!
            if (frame!!.buffer != 0 && !firstFrame)
                sb.append('^').append(getNoteLength(frame.buffer))
            // if we're on the first frame, we'll start out with a rest
            // equal to the length of the buffer.
            if (firstFrame) {
                if (frame.buffer != 0)
                    sb.append('r').append(getNoteLength(frame.buffer))
            }
            // if we're not on the first frame and there's no buffer,
            // lets make a new line for sanity's sake.
            if (!firstFrame && frame.buffer == 0)
                sb.append("\n").append('\'').append(chan).append(' ')
            // an iterator through all the notes...
            for (i in 0 until frame.notes!!.size) {
                // get the current note
                val n = frame.notes!![i]
                // Effects
                if (n.effects.isNotEmpty()) {
                    // if there are effects...
                    loop@ for (e in n.effects) {
                        val effect = e!!.type[0]
                        when (effect) {
                            'W' -> sb.append(" L ")
                            'A' -> {
                                sb.append(" k")
                                if (n.volume == -1) {
                                    if (previousNote.volume == -1)
                                        break@loop
                                    sb.append(previousNote.volume)
                                } else {
                                    sb.append(n.volume)
                                }
                                sb.append(",")
                                val direction = e.getParam(1) - e.getParam(0) > 0
                                if (direction)
                                    sb.append('0')
                                else
                                    sb.append('1')
                                sb.append(',')
                                var speed = Math.abs(e.getParam(1) - e.getParam(0))
                                if (speed != 0) {
                                    speed /= 2
                                    speed = 7 - speed
                                    if (speed == 0)
                                        speed = 1
                                }
                                sb.append(speed)
                            }
                            'J' -> {
                                sb.append(" p")
                                if (e != null) {
                                    if (e.getParam(0) == 0 && e.getParam(1) == 0) {
                                        sb.append("0")
                                    } else if (e.getParam(0) != 0 && e.getParam(1) == 0) {
                                        sb.append("1")
                                    } else if (e.getParam(0) == 0 && e.getParam(1) != 0) {
                                        sb.append("2")
                                    } else {
                                        sb.append("3")
                                    }
                                }
                            }
                            'H' -> {
                                sb.append(" @ v")
                                if (e != null) {
                                    sb.append(e.getParam(0) / 2)
                                }
                                sb.append(",")
                                if (e != null) {
                                    sb.append(e.getParam(1) / 2)
                                }
                            }
                        }
                    }
                }
                // if the note has a volume set, push it to the output
                if (n.volume != -1)
                    sb.append(" v").append(n.volume).append(' ')
                // if the current note's instrument is not blank and not equal to the
                // previous note's instrument, we need to update the instruments!
                if (forceInstrumentCheck && previousNote.instrument != -1) {
                    val instrument = getInstrumentById(previousNote.instrument)
                    val volume = getVolumeMacroById(instrument!!.volume)
                    // set the volume macro
                    if (volume != null)
                        sb.append(sb_volumeMML(volume))
                }
                if (n.instrument != -1 && previousNote.instrument != n.instrument) {
                    val instrument = getInstrumentById(n.instrument)
                    val volume = getVolumeMacroById(instrument!!.volume)
                    // set the volume macro
                    if (volume != null)
                        sb.append(sb_volumeMML(volume))
                    else
                        sb.append(" zv127,0,0")
                }
                // if the current note is empty (which means something else was set)
                // we slur it and set the length to the empty note.
                when {
                    n.note.isEmpty() -> {
                        sb.append(" & ")
                        sb.append(noiseFtm2Gbmc(previousNote.note.toLowerCase()))
                        sb.append(getNoteLength(n.length))
                    }
                    n.note == "---" -> // if it's a note cut...
                        sb.append(" r").append(getNoteLength(n.length))
                    n.note == "===" -> {
                        // if it's a note release
                        val instrument = getInstrumentById(previousNote.instrument)
                        val volume = getVolumeMacroById(instrument!!.volume + 64)
                        // set the volume macro
                        if (volume != null)
                            sb.append(sb_volumeMML(volume))
                        // and send the note to the output.
                        sb.append(' ')
                        sb.append(noiseFtm2Gbmc(previousNote.note.toLowerCase())).append(getNoteLength(n.length))
                    }
                    else -> {
                        // otherwise we'll output the note like normal
                        sb.append(' ')
                        sb.append(noiseFtm2Gbmc(n.note.toLowerCase())).append(getNoteLength(n.length))
                    }
                }
                // if the note is not a cut or note-off, then we will
                // set it equal to the previous note.
                if (!(n.note == "===" || n.note == "---" || n.note.isEmpty()))
                    previousNote = n
            }
            firstFrame = false
        }
        return sb
    }

    private fun noiseFtm2Gbmc(gbmc: String): String {
        val pitch = Integer.parseInt(gbmc, 16)
        return "w" + (15 - pitch) + ",0,0 c"
    }

    private fun noteToNum(note: String): Int {
        when (note) {
            "c" -> return 0
            "c+" -> return 1
            "d" -> return 2
            "d+" -> return 3
            "e" -> return 4
            "f" -> return 5
            "f+" -> return 6
            "g" -> return 7
            "g+" -> return 8
            "a" -> return 9
            "a+" -> return 10
            "b" -> return 11
        }
        return -1
    }

    private fun numToNote(num: Int): String {
        var num = num
        var oct = ""
        var deoct = ""
        if (num < 0) {
            oct = "<"
            deoct = ">"
        }
        if (num <= -12) {
            oct = "<<"
            deoct = ">>"
        }
        if (num >= 12) {
            oct = ">"
            deoct = "<"
        }
        if (num >= 24) {
            oct = ">>"
            deoct = "<<"
        }
        num %= 12
        if (num < 0)
            num += 12
        when (num) {
            0 -> return oct + "c" + deoct
            1 -> return oct + "c+" + deoct
            2 -> return oct + "d" + deoct
            3 -> return oct + "d+" + deoct
            4 -> return oct + "e" + deoct
            5 -> return oct + "f" + deoct
            6 -> return oct + "f+" + deoct
            7 -> return oct + "g" + deoct
            8 -> return oct + "g+" + deoct
            9 -> return oct + "a" + deoct
            10 -> return oct + "a+" + deoct
            11 -> return oct + "b" + deoct
        }
        return ""
    }

    private fun sb_volumeMML(v: MacroVolume, isTriangle: Boolean = false): StringBuilder {
        val sb = StringBuilder()
        sb.append(" zv")
        sb.append(v.ident)
        sb.append(",1,0")
        return sb
    }

    private fun sb_pitchMML(p: MacroPitch): StringBuilder {
        val sb = StringBuilder()
        sb.append(" zf")
        sb.append(p.ident)
        sb.append(",1,0")
        return sb
    }

    private fun sb_dutyMML(d: MacroDuty): StringBuilder {
        val sb = StringBuilder()
        sb.append(" zw")
        sb.append(d.ident)
        sb.append(",1,0")
        return sb
    }

    private fun getNoteLength(length: Int): String {
        var s = ""
        for (i in 0 until length) {
            s += "64^"
        }
        s = s.replace("64\\^64\\^".toRegex(), "32^")
        s = s.replace("32\\^32\\^".toRegex(), "16^")
        s = s.replace("16\\^16\\^".toRegex(), "8^")
        s = s.replace("8\\^8\\^".toRegex(), "4^")
        s = s.replace("4\\^4\\^".toRegex(), "2^")
        s = s.replace("2\\^2\\^".toRegex(), "1^")
        s = s.substring(0, s.length - 1)
        return s
    }

    private fun sb_MacroVolume(): StringBuilder {
        val sb = StringBuilder()
        //#V0 {0, -1, -2, -3, [-4, -5, -6] 2,] 2}
        for (v in volumeMacros) {
            val num = v.ident
            sb.append("#V").append(num).append(" {")
            var loopPoint = v.values.size
            if (v.release != -1)
                loopPoint = v.release
            for (i in 0 until loopPoint) {
                if (i == v.loop)
                    sb.append("[,")
                val value = v.values[i]
                sb.append(value - 15)
                when {
                    i < loopPoint - 1 -> sb.append(", ")
                    v.loop != -1 -> sb.append(",] 2,] 2}\n")
                    else -> sb.append(",\\}\n")
                }
            }
            if (v.release != -1) {
                sb.append("#V").append(num + 64).append(" {")
                for (i in v.release until v.values.size) {
                    if (i == v.loop)
                        sb.append("[,")
                    val value = v.values[i]
                    sb.append(value - 15)
                    when {
                        i < v.values.size - 1 -> sb.append(", ")
                        v.loop != -1 -> sb.append(",] 2,] 2}")
                        else -> sb.append(",\\}\n")
                    }
                }
                sb.append(" ; Release of ").append(num).append("\n")
            }
        }
        return sb
    }

    private fun sb_MacroDuty(): StringBuilder {
        val sb = StringBuilder()
        //#V0 {0, -1, -2, -3, [-4, -5, -6] 2,] 2}
        for (d in dutyMacros) {
            val num = d.ident
            sb.append("#X").append(num).append(" {")
            var loopPoint = d.values.size
            if (d.release != -1)
                loopPoint = d.release
            for (i in 0 until loopPoint) {
                if (i == d.loop)
                    sb.append("[,")
                val value = d.values[i]
                sb.append(value)
                when {
                    i < loopPoint - 1 -> sb.append(", ")
                    d.loop != -1 -> sb.append(",] 2,] 2}\n")
                    else -> sb.append(",\\}\n")
                }
            }
            if (d.release != -1) {
                sb.append("#X").append(num + 64).append(" {")
                for (i in d.release until d.values.size) {
                    if (i == d.loop)
                        sb.append('[')
                    val value = d.values[i]
                    sb.append(value)
                    when {
                        i < d.values.size - 1 -> sb.append(", ")
                        d.loop != -1 -> sb.append(",] 2,] 2}")
                        else -> sb.append(",\\}\n")
                    }
                }
                sb.append(" ; Release of ").append(num).append("\n")
            }
        }
        return sb
    }

    private fun sb_MacroPitch(): StringBuilder {
        val sb = StringBuilder()
        //#V0 {0, -1, -2, -3, [-4, -5, -6] 2,] 2}
        for (p in pitchMacros) {
            val num = p.ident
            sb.append("#F").append(num).append(" {")
            var loopPoint = p.values.size
            if (p.release != -1)
                loopPoint = p.release
            for (i in 0 until loopPoint) {
                if (i == p.loop)
                    sb.append("[,")
                val value = p.values[i]
                sb.append(value)
                when {
                    i < loopPoint - 1 -> sb.append(", ")
                    p.loop != -1 -> sb.append(",] 2,] 2}\n")
                    else -> sb.append(",\\}\n")
                }
            }
            if (p.release != -1) {
                sb.append("#F").append(num + 64).append(" {")
                for (i in p.release until p.values.size) {
                    if (i == p.loop)
                        sb.append('[')
                    val value = p.values[i]
                    sb.append(value)
                    when {
                        i < p.values.size - 1 -> sb.append(", ")
                        p.loop != -1 -> sb.append("] 2,] 2}")
                        else -> sb.append(",\\}\n")
                    }
                }
                sb.append(" ; Release of ").append(num).append("\n")
            }
        }
        return sb
    }

    companion object {

        private fun printHelp() {
            println("usage: java -jar FTM2GBMC.jar [input] [output]")
        }

        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val sc = Scanner(System.`in`)
            var input: String
            println("------------------------")
            println("|      Welcome to      |")
            println("| Savestate's FTM2GBMC |")
            println("------------------------")
            if (args.isNotEmpty()) {
                input = args[0]
                if (input == "-h") {
                    printHelp()
                    System.exit(0)
                }
            } else {
                print("[Open] FamiTracker text export --> ")
                input = sc.nextLine()
            }
            input = input.replace("\\\"".toRegex(), "")
            val encoding = Charset.defaultCharset()
            val lines = Files.readAllLines(Paths.get(input), encoding) as ArrayList<String>
            val ftm2gbmc = FTM2GBMC(lines)
            input = if (args.size > 1) {
                args[1]
            } else {
                print("[Save] GBMC .mml filename --> ")
                sc.nextLine()
            }
            val output = ftm2gbmc.build()
            val out = PrintWriter(input)
            out.print(output)
            out.close()
        }
    }

}
