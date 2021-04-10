all: compile

compile:
	javac Broker/*.java
	javac -classpath ../jar/json-20210307.jar:../jar/json-simple-1.1.jar: Server/*.java

kvBroker:
	java Broker.Broker $(arguments)

kvServer:
	java -cp .:../jar/json-20210307.jar:../jar/json-simple-1.1.jar Server.Server $(arguments)

clean:
	rm -f Broker/*.class *~
	rm -f Server/*.class *~
	rm -f *.class *~
