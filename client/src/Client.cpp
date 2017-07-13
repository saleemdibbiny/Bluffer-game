#include "../headers/Client.h"
#include <stdlib.h>
#include <boost/locale.hpp>
#include "../headers/connectionHandler.h"
#include "../headers/utf8.h"
#include "../headers/encoder.h"
#include <boost/thread.hpp>
#include <iostream>


void listenToSocket(ConnectionHandler *handler_)
{
	while (1) {

		std::string answer;
		if (!handler_->getLine(answer)) {
			std::cout << "Disconnected. Exiting...\n" << std::endl;
			break;
		}
		int len = answer.length();
		answer.resize(len - 1);
		if (answer == "SYSMSG QUIT ACCEPTED" || answer == "SYSMSG QUIT ACCEPTED\r") {
			break;
		}
		std::cout << answer << std::endl;

	}

}

void standardInputStream(ConnectionHandler *handler_)
{
	while (1) {

		const short bufsize = 1024;
		char buf[bufsize];
		std::cin.getline(buf, bufsize);
		std::string line(buf);
		if (!handler_->sendLine(line) || line.compare("QUIT") == 0) {
			std::cout << "Disconnected. Exiting...\n" << std::endl;
			break;
		}
	}
}



int main(int argc, char *argv[]) {

	std::string host = argv[1];
	short port = atoi(argv[2]);

	ConnectionHandler *connectionHandler = new ConnectionHandler(host, port);
	if (!connectionHandler->connect()) {
		std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
		delete connectionHandler;
		return 1;
	}

	boost::thread listenToServer(listenToSocket, connectionHandler);
	boost::thread listenToUser(standardInputStream, connectionHandler);
	listenToServer.join();
	listenToUser.join();

	connectionHandler->close();
	delete connectionHandler;
	return 0;


}




