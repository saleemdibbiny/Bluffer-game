# All Targets
all: client

# Executable "client" depends on the files Client.o and connectionHandler.o.
client: bin/Client.o bin/connectionHandler.o
	@echo 'Building target: client'
	@echo 'Invoking: C++ Linker'
	g++ -o bin/client bin/Client.o bin/connectionHandler.o -lboost_system -lboost_locale -lboost_thread
	@echo 'Finished building target: client'

bin/Client.o: src/Client.cpp
	g++ -g -Wall -Weffc++ -c -Linclude -o bin/Client.o src/Client.cpp

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ -g -Wall -Weffc++ -c -Linclude -o bin/connectionHandler.o src/connectionHandler.cpp

#Clean the build directory
clean: 
	rm -f bin/*