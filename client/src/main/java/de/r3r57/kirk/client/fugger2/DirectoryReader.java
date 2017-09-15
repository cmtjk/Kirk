package de.r3r57.kirk.client.fugger2;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class DirectoryReader {

    List<Path> filePathList;

    public List<Path> getMissionList(String difficulty) throws IOException {

        System.out.println(Paths.get(getClass().getClassLoader().getResource("fugger2/" + difficulty).toString()));
        filePathList = new LinkedList<>();
        iterate(difficulty);
        return filePathList;
    }

    private void iterate(String difficulty) throws IOException {
        try (DirectoryStream<Path> dirStream = Files
                .newDirectoryStream(Paths.get(getClass().getClassLoader().getResource("fugger2/" + difficulty).getPath()))) {

            for (Path entry : dirStream) {
                if (Files.isRegularFile(entry) && entry.toString().toLowerCase().endsWith(".png")
                        && !Files.isHidden(entry) && Files.isReadable(entry) && !Files.isSymbolicLink(entry)) {
                    filePathList.add(entry.toAbsolutePath());
                        }
            }
                }
    }

}
