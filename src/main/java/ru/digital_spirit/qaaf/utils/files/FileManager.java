package ru.digital_spirit.qaaf.utils.files;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;

public class FileManager {
    private FileManager() {}

    /**
     * Метод для получения содержимого файла по указанному пути в виде строки
     * @param filePath - путь до файла
     * @return - возвращает строку с содержимым файла
     */
    public static String getFileContent(String filePath) {
        Path path = Path.of(filePath);
        try(Scanner scanner = new Scanner(new FileReader(path.toString()))) {
            StringBuilder out = new StringBuilder();
            while (scanner.hasNextLine()) {
                String str = new String(scanner.nextLine().getBytes(), StandardCharsets.UTF_8);
                out.append(str).append(System.lineSeparator());
            }
            return out.toString();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
