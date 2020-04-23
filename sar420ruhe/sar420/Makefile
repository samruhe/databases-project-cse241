# Makefile for 241 Project

JCC = javac
JARFILE = sar420.jar

all:
	$(JCC) $$(find ./* | grep .java)
	jar cfmv $(JARFILE) Manifest.txt -C . .

run:
	java -jar $(JARFILE)

clean:
	rm -f $$(find ./* | grep .class)