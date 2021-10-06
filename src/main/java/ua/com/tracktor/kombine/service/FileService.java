package ua.com.tracktor.kombine.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

@Service
public class FileService {
    @Value("${spring.resources.static-locations}")
    private String staticResourcesLocation;

    @Value("${files.path}")
    private String filesPath;

    @Value("${spring.mvc.static-path-pattern}")
    private String staticPathPattern;

    private String filesFullPath;
    private String staticResourcesPath;

    @PostConstruct
    public void init() {
        // Find first static resources' folder located on local filesystem and use it for storing files
        filesFullPath = getFilesFullPath();

        if (staticPathPattern.endsWith("**")) {
            staticResourcesPath = staticPathPattern.substring(0, staticPathPattern.length() - 2);
        } else if (staticPathPattern.endsWith("*")) {
            staticResourcesPath = staticPathPattern.substring(0, staticPathPattern.length() - 1);
        } else {
            staticResourcesPath = staticPathPattern;
        }

        // Creating folder if missing
        try {
            Files.createDirectories(Paths.get(filesFullPath));
        } catch (IOException e) {
            // Application should be stated in any case

            System.err.println("Couldn't initialize file's store folder:" + e.toString());
        }
    }

    public String saveFile(String filePath, InputStream fileData) {
        try {
            Path root = Paths.get(filesFullPath);
            if (!Files.exists(root)) {
                init();
            }

            Path fileDataPath = Paths.get(filesFullPath + filePath);
            if (!Files.exists(fileDataPath.getParent())) {
                Files.createDirectories(fileDataPath.getParent());
            }

            Files.deleteIfExists(fileDataPath);
            Files.copy(fileData, fileDataPath);

            String uploadedFilePath = staticResourcesPath + filesPath + filePath;

            return uploadedFilePath.replace('\\', '/');
        } catch (Exception e) {
            System.err.println("Could not store the file. Error: " + e.toString());
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    private String getFilesFullPath() {
        Optional<String> elementData = Arrays.stream(staticResourcesLocation.split(",")).filter(element -> element.startsWith("file:")).findFirst();
        if (elementData.isEmpty()) {
            throw new RuntimeException("No static resources folder found on OS file-system");
        }

        return elementData.get().substring(5) + filesPath;
    }
}
