JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
		Belt.java \
		BeltMover.java \
		Bicycle.java \
		BicycleHandlingThread.java \
		Consumer.java \
        DefKnownException.java \
		DefException.java \
		HandlingException.java \
		Inspector.java \
		OverloadException.java \
		Params.java \
		Producer.java \
		Robot.java \
		Sensor.java \
		SensorException.java \
		ShortBelt.java \
		ShortBeltConsumer.java \
		ShortBeltMover.java \
        Sim.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class