package de.r3r57.kirk.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.StringTokenizer;

public class Model extends Observable {

    private String server;
    private int port;
    private String username;
    private Socket socket;
    private Thread readerThread;

    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public void connect(String server, String port, String username) throws IOException {
        this.server = server;
        try {
            this.port = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        this.username = username;

        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(this.server, this.port), 5000);
            setChanged();
            notifyObservers("Connected to " + socket.getInetAddress() + ":" + socket.getLocalPort()
                    + "\nInfo: Use /name <name> to change your username.\nUse /f2mission <easy|medium|hard> to get a random Fugger 2 mission.");
            createStreams(socket);

        } catch (IOException e) {
            throw e;
        }
    }

    private void createStreams(Socket socket) throws IOException {
        try {
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());

            changeName(username);
            createReaderThread(inputStream);

        } catch (IOException e) {
            throw e;
        }
    }

    private void createReaderThread(DataInputStream inputReader) {
        readerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    StringTokenizer tokenizer = new StringTokenizer(inputReader.readUTF());
                    switch (tokenizer.nextToken()) {
                        case "KIRK_MESSAGE":
                            String message = "";
                            while (tokenizer.hasMoreTokens()) {
                                message += " " + tokenizer.nextToken();
                            }
                            setChanged();
                            notifyObservers(message);
                            break;
                        case "KIRK_USERS":
                            List<String> onlineUsers = new LinkedList<>();
                            while (tokenizer.hasMoreTokens()) {
                                onlineUsers.add(tokenizer.nextToken());
                            }
                            setChanged();
                            notifyObservers(onlineUsers);
                            break;
                        case "KIRK_F2MISSION":
                            String mission = "";
                            while (tokenizer.hasMoreTokens()) {
                                mission += " " + tokenizer.nextToken();
                            }
                            String[] missionArray = mission.split(" # ");
                            String missionMessage = "\n[F2MISSION] Your mission is::\n" + missionArray[0] + "\n"
                                + missionArray[1] + "\n" + "\t" + missionArray[2] + "\n";
                            setChanged();
                            notifyObservers(missionMessage);
                            break;

                    }
                } catch (IOException e) {
                    forcedDisconnect();
                }
            }
        });

        readerThread.start();
    }

    private void forcedDisconnect() {
        try {
            readerThread.interrupt();
            socket.close();
            setChanged();
            notifyObservers(new Error("You have been disconnected from the server."));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() throws IOException {
        outputStream.writeUTF("KIRK_LOGOUT");
        outputStream.flush();
        readerThread.interrupt();
        socket.close();

    }

    public void sendMessage(String message) throws IOException {
        if (message != null) {
            outputStream.writeUTF("KIRK_MESSAGE " + message);
            outputStream.flush();
        }

    }

    public void changeName(String message) throws IOException {
        if (message != null) {
            outputStream.writeUTF("KIRK_NAME " + message);
            outputStream.flush();
        }

    }

    public void kick(String name) throws IOException {
        if (name != null) {
            outputStream.writeUTF("KIRK_KICK " + name);
            outputStream.flush();
        }
    }

    public boolean getF2Mission(String difficulty) throws IOException {
        if (difficulty != null
                && ("easy".equals(difficulty) || "medium".equals(difficulty) || "hard".equals(difficulty))) {
            outputStream.writeUTF("KIRK_F2MISSION " + difficulty);
            outputStream.flush();
            return true;
        } else {
            setChanged();
            notifyObservers("f2mission: Invalid argument. Expected <easy|medium|hard>");
            return false;
        }
    }
}
