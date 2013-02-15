SABLECCJAR=sablecc.jar
Promela.jar: promela
	jar cvfm Promela.jar main/mymanifest main/ promela/ visitors/ de/
promela: *.scc
	rm -rf promela
	java -jar ${SABLECCJAR} promela.scc
	javac -target 1.5 promela/*/*.java
	javac -target 1.5 main/*.java
	jar cvfm Promela.jar main/mymanifest main/ promela/ visitors/ de/