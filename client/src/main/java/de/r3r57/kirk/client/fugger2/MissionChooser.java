package de.r3r57.kirk.client.fugger2;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

public class MissionChooser {

    DirectoryReader dirReader;
    List<Path> easy, medium, hard;
    Random rand;

    public MissionChooser() throws IOException {
        dirReader = new DirectoryReader();
        rand = new Random();
        easy = dirReader.getMissionList("easy");
        medium = dirReader.getMissionList("medium");
        hard = dirReader.getMissionList("hard");
    }

    public Path getMission(String difficulty) {
        switch (difficulty) {
            case "easy":
                return getRandomMission(easy);
            case "medium":
                return getRandomMission(medium);
            case "hard":
                return getRandomMission(hard);

        }
        throw new IllegalArgumentException();
    }

    private Path getRandomMission(List<Path> list) {

        int max = list.size();

        Path missionPath = list.get(rand.nextInt(max));

        return missionPath;
    }

}
