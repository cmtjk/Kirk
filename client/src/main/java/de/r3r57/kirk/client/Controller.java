package de.r3r57.kirk.client;

import java.io.IOException;

import de.r3r57.kirk.client.fugger2.MissionChooser;
import de.r3r57.kirk.client.fugger2.MissionViewer;

public class Controller {

    View view;;
    Model model;

    MissionViewer f2view;
    MissionChooser f2chooser;

    public Controller(View view, Model model) {
        this.model = model;
        this.view = view;

        try {
            f2view = new MissionViewer();
            f2chooser = new MissionChooser();
        } catch (IOException e) {
            view.error("Unable to initialize Fugger 2.");
        }

        view.show();
    }

    public boolean connect(String server, String port) {
        try {
            model.connect(server, port, "User");
            return true;
        } catch (IOException e) {
            view.error("Unable to connect so server " + server + ":" + port + "\n");
            return false;
        }
    }

    public boolean disconnect() {
        try {
            model.disconnect();
            return true;
        } catch (IOException e) {
            view.error("Unable to disconnect from sserver.\n");
            return false;
        }

    }

    public void sendMessage(String message) {
        try {
            model.sendMessage(message);
        } catch (IOException e) {
            view.error("Unable to send message.\n");
            e.printStackTrace();
        }
    }

    public void changeName(String name) {
        try {
            model.changeName(name);
        } catch (IOException e) {
            view.error("Unable to change name.\n");
        }
    }

    public void kick(String name) {
        try {
            model.kick(name);
        } catch (IOException e) {
            view.error("Unable to kick:" + name + "\n");
        }
    }

    public void getF2Mission(String difficulty) {
        try {
            if (model.getF2Mission(difficulty)) {
                f2view.setImage(f2chooser.getMission(difficulty));
                f2view.show();
            }
        } catch (IOException e) {
            view.error("Unable to broadcast Fugger 2 mission: " + e);
        }

    }

}
