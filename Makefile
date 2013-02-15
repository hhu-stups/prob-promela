SABLECCJAR=sablecc.jar
Promela.jar: promela
	jar cvfm Promela.jar main/mymanifest main/ promela/ visitors/ de/
promela: *.scc
	rm -rf Promela.jar promela
	java -jar ${SABLECCJAR} promela.scc
	javac -cp ".:anarres-cpp.jar" -target 1.5 promela/*/*.java
	javac -cp ".:anarres-cpp.jar" -target 1.5 main/*.java
	jar cvfm Promela.jar main/mymanifest main/ promela/ visitors/ de/