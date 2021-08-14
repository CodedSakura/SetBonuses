package eu.codedsakura.setbonuses.config;

import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigReader {
    private final File file;
    private final String filename;

    public ConfigReader(Path dir, String filename) {
        this.filename = filename;
        this.file = dir.resolve(filename).toFile();
    }

    public Config read() throws Exception {
        if (!file.exists()) createFromResources();
        Gson gson = new Gson();
        return gson.fromJson(Files.newBufferedReader(file.toPath()), Config.class);
    }

    private void createFromResources() throws ConfigParserException, IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);
        if (inputStream == null) {
            throw new ConfigParserException("File " + filename + " not found in the resources folder!");
        }

        byte[] buffer = new byte[inputStream.available()];
        if (inputStream.read(buffer) != buffer.length) {
            throw new ConfigParserException("Buffer length mismatch while reading resources/" + filename + "!");
        }

        if (file.createNewFile()) {
            OutputStream outputStream = new FileOutputStream(file);
            outputStream.write(buffer);
        } else {
            throw new ConfigParserException("Cannot init config: File already exists!");
        }
    }

    private static class ConfigParserException extends RuntimeException {
        public ConfigParserException(String text) {
            super(text);
        }
    }
}
