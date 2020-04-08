# Makefile for 241 Project
#
JCC = javac
JARFILE = myProject

sources = $(wildcard *.java)
classes = $(sources:.java=.class)

all: $(JARFILE)

$(JARFILE): $(classes)
	@echo "Main-Class: Main" >> Manifest.txt
	@echo "Class-Path: ojdbc8.jar" >> Manifest.txt
	@echo "" >> Manifest.txt
	jar cfmv $(JARFILE) Manifest.txt $(classes)

%.class: %.java
	$(JCC) $<

run:
	java -jar $(JARFILE)

clean:
	rm -f *.class
	rm Manifest.txt