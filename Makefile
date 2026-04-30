V=1.0.0-SNAPSHOT
JAR=build/libs/eselling-admin-${V}.jar

run:
	gradle run

install:	${JAR}
	cp $? ~/lib

${JAR}:
	gradle jar
