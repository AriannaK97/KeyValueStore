all: compile

compile:
	javac Broker/*.java Server/*.java #*.java

kvBroker:
	java Broker.Broker

kvServer:
	java Server.Server

clean:
	rm -f Broker/*.class *~
	rm -f Server/*.class *~
	rm -f *.class *~