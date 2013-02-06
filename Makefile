SABLECCJAR=sablecc.jar
Promela.jar: promela
	jar cvfm Promela.jar main/mymanifest main/ promela/ visitors/ de/
promela: *.scc
	rm -rf promela
	java -jar ${SABLECCJAR} promela.scc
	javac promela/*/*.java
	javac main/*.java
	jar cvfm Promela.jar main/mymanifest main/ promela/ visitors/ de/