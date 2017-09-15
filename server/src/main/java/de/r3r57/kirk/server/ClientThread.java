package de.r3r57.kirk.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

public class ClientThread extends Thread {

    private Model server;
    private Socket socket;

    private String username = "Unknown";

    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public ClientThread(Model server, Socket socket) {
        this.server = server;
        this.socket = socket;
        display(socket.toString() + " connected.", true);
    }

    @Override
    public void run() {
        try {
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());

            broadcast(username + " connected.");

            receiveFromClient(inputStream);

        } catch (IOException e) {
            display("Unable to initalize reader/writer in client: " + e, true);
        }

    }

    private void receiveFromClient(DataInputStream inputReader) {
        try {
            String message;
            while (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
                StringTokenizer tokenizer = new StringTokenizer(inputStream.readUTF());
                switch (tokenizer.nextToken()) {
                    case "KIRK_MESSAGE":
                        message = "";
                        while (tokenizer.hasMoreTokens()) {
                            message += " " + tokenizer.nextToken();
                        }
                        broadcast("> " + username + ": " + message);
                        break;
                    case "KIRK_LOGOUT":
                        closeSocket();
                        break;
                    case "KIRK_NAME":
                        String tempUsername = username;
                        if (tokenizer.hasMoreTokens()) {
                            username = tokenizer.nextToken();
                        }
                        broadcast(tempUsername + " changed name to " + username + ".");
                        break;
                    case "KIRK_KICK":
                        server.kick(tokenizer.nextToken());
                        break;
                    case "KIRK_F2MISSION":
                        if (tokenizer.hasMoreTokens()) {
                            String difficulty = tokenizer.nextToken();
                            broadcast(username + " received a mission (" + difficulty + ") for Fugger 2.");
                        }
                        break;
                }
            }
            socket.close();

        } catch (IOException e) {
            display(username + " lost connection.", true);
        }

    }

    private void display(String message, boolean serverMessage) {
        server.display(message, serverMessage);
    }

    private void broadcast(String message) {
        server.broadcast(message);
    }

    public boolean writeMessage(String message) {
        try {
            if (socket.isClosed()) {
                return false;
            } else {
                outputStream.writeUTF(message);
                outputStream.flush();
                return true;
            }
        } catch (IOException e) {
            server.display("Unable to write message.", true);
            return false;
        }
    }

    public String getUsername() {
        return username;
    }

    public boolean socketClosed() {
        return socket.isClosed();
    }

    Socket getSocket() {
        return socket;
    }

    void closeSocket() throws IOException {
        socket.close();
        broadcast(username + " disconnected.");
    }

}
