# Bluffer-game #
<b>SPL: Assignment 3</b><br />
Java Concurrency and Synchronization<br /><br />

## Description ##
The Bluffer game is a type of trivia game, with a twist - the players try to fool each other into choosing absurd
answers. The game host asks a series of questions, for which the players try to provide answers that seem real.
The players are then presented with both the real answer, and the fake answers provided by other players, and
have to choose the real one. <br />
Players are awarded 10 points for choosing the correct answer, and 5 for each player that chose one of their fake
answers.<br />

## Implementation ##An implemention a text-based game server and client. The communication between the
server and the client(s) is performed using a simple text based protocol (TBGP), which can potentially
support different games; However, we will only support a single game - Bluffer. <br/>
The implementation of the server is based on a Reactor and Thread-Per-Client servers. Adjusted to support complex protocol TBGP, and support the Bluffer game.<br /> <br />
* The server is written in Java. 
* The client is written in C++ with BOOST.
* Using maven as a build tool.

### Server ###
An implementation of a single protocol, supporting both the Thread-Per-Client and Reactor servers.
### Client ###
The client is multithreaded with one thread for handling the socket and another thread to handle stdin.<br />
The client receives the server’s IP and PORT as arguments.

