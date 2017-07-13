package threadPerClient;

import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import app.BlufferConfig;
import app.BlufferGame;
import app.BlufferPlayer;
import app.BlufferQuestion;
import protocol.AfterGameOverCallback;
import protocol.Game;
import protocol.GameFactory;
import protocol.ServerProtocol;
import protocol.TextBasedGameDatabase;
import tokenizer.CommandMessage;

class MultipleClientProtocolServer implements Runnable {
	private ServerSocket serverSocket;
	private int listenPort;
	private ServerProtocolFactory<CommandMessage> factory;
	
	
	public MultipleClientProtocolServer(int port, ServerProtocolFactory<CommandMessage> p)
	{
		serverSocket = null;
		listenPort = port;
		factory = p;
	}
	
	public void run()
	{
		try {
			serverSocket = new ServerSocket(listenPort);
			System.out.println("Listening...");
		}
		catch (IOException e) {
			System.out.println("Cannot listen on port " + listenPort);
		}
		
		while (true)
		{
			try {
				ConnectionHandler newConnection = new ConnectionHandler(serverSocket.accept(), factory.create());
            new Thread(newConnection).start();
			}
			catch (IOException e)
			{
				System.out.println("Failed to accept on port " + listenPort);
			}
		}
	}
	

	// Closes the connection
	public void close() throws IOException
	{
		serverSocket.close();
	}
	 private static class AllQuestions {
	    	BlufferQuestion[] questions;
	    }
	public static void main(String[] args) throws IOException
	{
		
		if (args.length == 0) {
            System.err.println("Usage: java MultipleClientProtocolServer <port>");
            System.exit(1);
        }

        try {
    		// Get port
    		int port = Integer.decode(args[0]).intValue();

            TextBasedGameDatabase.getInstance().addGame("BLUFFER", new GameFactory<BlufferPlayer>() {
				@Override
				public Game<BlufferPlayer> create(AfterGameOverCallback onEnd) {
					return new BlufferGame(onEnd);
				}
            	
			});
            Gson gson = new Gson();
    		JsonReader reader = new JsonReader(new FileReader("src/bluffer.json"));
    		 AllQuestions data = gson.fromJson(reader, AllQuestions.class);
    		for(int i = 0; i< data.questions.length; i++){
    			BlufferConfig.addQuestion(data.questions[i]);
    		}
    		
    		
    		MultipleClientProtocolServer server = new MultipleClientProtocolServer(port,new ServerProtocolFactory<CommandMessage>() {
    			@Override
    			public ServerProtocol<CommandMessage> create() {
    				return new TBGP();
    			}
    		});
    		Thread serverThread = new Thread(server);
    		serverThread.start();
    		try {
    			serverThread.join();
    		}
    		catch (InterruptedException e)
    		{
    			System.out.println("Server stopped");
    		}
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		
		
		
				
	}
}
