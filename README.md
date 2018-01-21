## FTM2GBMC: *Make Game Boy Music in FamiTracker* [![Build Status](https://travis-ci.org/Savestate2A03/FTM2GBMC.svg?branch=master)](https://travis-ci.org/Savestate2A03/FTM2GBMC)

#### Welcome

Hey there! I finally got around to remaking my previous software that converted FamiTracker music from a text export to Gameboy music, this time much more functional than last time, and now on GitHub!

#### Usage

```sh
java -jar FTM2GBMC.jar [input [output]]
```

```input``` FamiTracker text export file.

```output``` GBMC compatible MML file.

*If no input and/or output is provided at the command line, it'll ask you for them during runtime.*

## Required Software

- Any Gradle-supporting IDE (Netbeans, Eclipse, or IntelliJ) [→](https://www.jetbrains.com/idea/)
- Game Boy Music Compiler [→](http://www.geocities.jp/submarine600/html/sounddriver.html)
- FamiTracker [→](http://www.famitracker.com/)

You can build the software using NetBeans or you can download a precompiled version on the Battle of the Bits dev thread [here](http://battleofthebits.org/academy/GroupThread/12151/FTM2GBMC+-+Make+Gameboy+Music+in+FamiTracker%21/)! *(not guaranteed to be up-to-date)*

## FamiTracker Module Specifications

Now given that this is a fairly new software, (and my n00b programming skillz) it's not fully featured, and has a few strange quirks which I will try my best to explain...

### Macros

Volume, pitch and duty macros work along with release points and loop points.
While volume macros *do* work, it's probably best to not be using them unless you like the sound of hardware resets all up in your square waves.
Lastly, arp macros do not work because GBMC does not support arp macros. It does however support inline arps (0xx), a goal to implement in the future.

### Commands

A few of the FamiTracker commands are 1:1 when it comes to how they translate over with FTM2GBMC. A lot of commands are **only** used in the translation process and have no effect on the FamiTracker module. Remember, when using this tool, you're composing for FTM2GBMC, *not FamiTracker*.

#### Global Commands

Global commands supported on all channels are ```Wxx```, ```Jxx```, and ```Hxx```

#### Pulse Channels

The pulse channels support the FamiTracker commands ```Axx```, ```Vxx```, ```Qxx```, ```Rxx```, and ```Pxx```.

#### Triangle Channel

The pulse channels support the FamiTracker commands ```Qxx```, ```Rxx```, and ```Pxx```.

#### Noise Channel

The noise channel supports the FamiTracker command ```Axx```.

#### IMPORTANT: Retriggering Volume Envlopes

All notes and effect commands retrigger each note's volume envlope. If you'd like to change the duty cycle/pitch mid-note, you'll need to use a duty macro.

```
C-3 00 A A02
D#3 00 - ---
G-3 00 - ---
C-3 00 - ---
D#3 00 - ---
G-3 00 - ---
C-3 00 - ---
D#3 00 - ---
G-3 00 - ---
C-3 00 - ---
```

... is the same as ...

```
C-3 00 A A02
D#3 00 A ---
G-3 00 A ---
C-3 00 A ---
D#3 00 A ---
G-3 00 A ---
C-3 00 A ---
D#3 00 A ---
G-3 00 A ---
C-3 00 A ---
```

... so you'll probably want to do something like ...

```
C-3 00 A ---
D#3 00 - ---
G-3 00 9 ---
C-3 00 - ---
D#3 00 8 ---
G-3 00 - ---
C-3 00 7 ---
D#3 00 - ---
G-3 00 6 ---
C-3 00 - ---
```

#### Qxx/Rxx

GBMC's pitch slide isn't based on a speed, it's based on a set note length, so pitch slide will take the entire length from when the slide was called to the next note. A way around this is to put down the note you're sliding to shortly after the slide that is called.

```
B-3 00 F Q41
... .. . ...
... .. . ...
... .. . ...
D#4 00 . ...
... .. . ...
... .. . ...
... .. . ...
G-4 00 . ...
... .. . ...
--- .. . ...
... .. . ...
A-4 00 . Q41
... .. . ...
=== .. . ...
... .. . ...
```

would be changed to

```
B-3 00 F Q41
... .. . ...
C-3 00 . ...
... .. . ...
D#4 00 . ...
... .. . ...
... .. . ...
... .. . ...
G-4 00 . ...
... .. . ...
--- .. . ...
... .. . ...
A-4 00 . Q41
... .. . ...
A#4 00 . ...
=== .. . ...
```

#### Jxx

The command usage is as follows: ```JLR```

```L``` is left channel enable.

```R``` is right channel enable.

##### Examples

```J01``` right channel

```J10``` left channel

```J11``` both channels

```J00``` no channels (aka mute)

```JB2``` == ```J11```

```J60``` == ```J10```

```J02``` == ```J01```

#### Hxx

The command usage is as follows: ```HLR```

`L` is the global left channel volume *(0-F, divided to 0-7)*

`R` is the global left channel volume *(0-F, divided to 0-7)*

When I say divided, GBMC's command parameters are 0-7, so...

0 = 0

1 = 0

2 = 1

3 = 1

4 = 2

5 = 2

etc etc

##### Examples

`H2F` very quiet left channel global volume, max volume right channel global volume

`H11` silent because 1 divided by 2 == 0 (think integer division)

`HFF` both channels are at full volume

Reminder, as with all effects, using an `Hxx` command will reset the volume envelope for the channel it's in (due to having to code the timing factor for the volume change). It's supported in all channels however so use it in whichever one would be least affected by an envelope reset.

##### GBS Playback Support for Hxx

As this is a hardware feature not supported by most GBS players, I suggest using [nezplug++](http://offgao.net/program/nezplug++.html)! It seemed to play the file back correctly. foo_gep however, **does not**!!
