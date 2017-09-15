package de.r3r57.kirk.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Model {

    private final String TITLE = "Kirk Server";
    private final String SOURCE = "[SERVER] ";

    private List<ClientThread> clientList;
    private int port;
    private SimpleDateFormat dateFormat;

    private String broadcastCurrentUsers;

    Model(int port) {
        this.port = port;
        dateFormat = new SimpleDateFormat("HH:mm:ss");
        clientList = new LinkedList<>();
    }

    void start() {
        display(TITLE, true);

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            display("Server started.", true);
            display("Server (" + serverSocket.getInetAddress() + ") waiting for Clients on port " + port + ".", true);

            acceptClients(serverSocket);

        } catch (IOException e) {
            display("Unable to create Server: " + e, true);
            System.exit(0);
        }
    }

    private void acceptClients(ServerSocket serverSocket) {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                createClientThread(this, socket);

            } catch (IOException e) {
                display("Could not accept client: " + e, true);
            }
        }
    }

    private void createClientThread(Model server, Socket socket) {
        ClientThread clientThread = new ClientThread(server, socket);
        clientList.add(clientThread);
        clientThread.start();
    }

    void display(String message, boolean serverMessage) {
        if (serverMessage) {
            System.out.println(SOURCE + message);
        } else {
            System.out.println(message);
        }
    }

    synchronized void kick(String name) {
        clientList.forEach(client -> {
            if (name.equals(client.getUsername())) {
                try {
                    client.closeSocket();
                    display("Kicked " + client.getUsername(), true);
                } catch (Exception e) {
                    display("Unable to kick " + client.getUsername(), true);
                }
            }
        });
    }

    synchronized void broadcast(String message) {
        broadcastCurrentUsers = "KIRK_USERS";
        clientList.forEach(client -> {
            if (!client.socketClosed()) {
                broadcastCurrentUsers += " " + client.getUsername();
            }
        });
        String broadcastMessage = "KIRK_MESSAGE " + dateFormat.format(new Date()) + " " + message;
        display(broadcastMessage, false);
        List<ClientThread> clientsToRemove = new ArrayList<>();
        clientList.forEach(client -> {
            if (!client.writeMessage(broadcastMessage) & !client.writeMessage(broadcastCurrentUsers)) {
                display(client.getUsername() + " removed from client list.", true);
                clientsToRemove.add(client);
            }
        });
        clientList.removeAll(clientsToRemove);
    }

}
