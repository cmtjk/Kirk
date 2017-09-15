package de.r3r57.kirk.server;

public class Main {

    public static void main(String[] args) {

	if (args.length < 1 || args.length > 1) {
	    System.out.println("Usage: java -jar KirkServer.jar <port>");
	    System.exit(0);
	}

	int portNumber = 0;

	try {
	    portNumber = Integer.parseInt(args[0]);

	} catch (NumberFormatException e) {
	    System.out.println("Usage: java -jar KirkServer.jar <port>");
	    System.exit(0);
	}

	new Model(portNumber).start();
    }

}
