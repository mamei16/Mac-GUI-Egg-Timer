#!/bin/bash


javac -XDignore.symbol.file=true TimerGUI.java
jar -cvfm Timer.jar manifest.mf *.class timer_icon.png TimerSound.wav
rm -f *.class
