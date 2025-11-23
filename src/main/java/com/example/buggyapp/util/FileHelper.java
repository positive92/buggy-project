package com.example.buggyapp.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHelper {

    // DEFECT: Resource leak - not closing streams
    public static String readFile(String filename) throws IOException {
        // DEFECT: Not using try-with-resources
        FileInputStream fis = new FileInputStream(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

        StringBuilder content = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }

        // DEFECT: Not closing reader and fis
        return content.toString();
    }

    // DEFECT: Path traversal vulnerability
    public static void writeFile(String filename, String content) throws IOException {
        // DEFECT: No validation of filename for path traversal
        // DEFECT: Hardcoded base path
        File file = new File("/tmp/uploads/" + filename);

        // DEFECT: Not using try-with-resources
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();
    }

    // DEFECT: Deleting files without confirmation
    public static boolean deleteFile(String filename) {
        // DEFECT: No security check before deletion
        // DEFECT: Direct file path manipulation
        File file = new File(filename);

        // DEFECT: Not checking if file exists before deletion
        return file.delete();
    }

    // DEFECT: Directory traversal vulnerability
    public static File[] listFiles(String directory) {
        // DEFECT: No validation of directory parameter
        File dir = new File(directory);

        // DEFECT: Not checking if directory exists or is a directory
        return dir.listFiles();
    }

    // DEFECT: Creating temp file insecurely
    public static File createTempFile(String prefix) throws IOException {
        // DEFECT: Predictable temp file name
        File tempFile = new File("/tmp/" + prefix + System.currentTimeMillis());

        // DEFECT: Not setting proper file permissions
        tempFile.createNewFile();

        return tempFile;
    }

    // DEFECT: File copy without proper error handling
    public static void copyFile(String source, String destination) {
        try {
            // DEFECT: Not validating paths
            Files.copy(Paths.get(source), Paths.get(destination));
        } catch (IOException e) {
            // DEFECT: Empty catch block
        }
    }

    // DEFECT: Reading entire file into memory
    public static byte[] readFileBytes(String filename) throws IOException {
        // DEFECT: No size check - could cause OutOfMemoryError
        File file = new File(filename);

        // DEFECT: Reading entire file into memory regardless of size
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);

        // DEFECT: Not closing stream
        return data;
    }

    // DEFECT: Hardcoded file paths
    private static final String LOG_FILE = "/var/log/application.log";
    private static final String DATA_DIR = "/opt/application/data";

    // DEFECT: Writing logs without rotation
    public static void appendToLog(String message) {
        try {
            // DEFECT: File could grow indefinitely
            FileWriter fw = new FileWriter(LOG_FILE, true);
            fw.write(message + "\n");
            fw.close();
        } catch (IOException e) {
            // DEFECT: Swallowing exception
            e.printStackTrace();
        }
    }

    // DEFECT: Insecure file permissions
    public static void createDirectory(String dirName) {
        // DEFECT: Creating directory without setting permissions
        File dir = new File(DATA_DIR + "/" + dirName);
        dir.mkdirs(); // DEFECT: Not checking return value
    }

    // DEFECT: Using File instead of Path (deprecated approach)
    public static boolean fileExists(String filename) {
        // DEFECT: Using old File API
        File file = new File(filename);
        return file.exists();
    }

    // DEFECT: Race condition between check and use
    public static void safeDelete(String filename) throws IOException {
        File file = new File(filename);

        // DEFECT: TOCTOU (Time-of-check to time-of-use) vulnerability
        if (file.exists()) {
            // DEFECT: File could be deleted/modified between check and delete
            file.delete();
        }
    }

    // DEFECT: Not handling symbolic links securely
    public static String getCanonicalPath(String filename) {
        try {
            File file = new File(filename);
            // DEFECT: Not validating result against allowed paths
            return file.getCanonicalPath();
        } catch (IOException e) {
            // DEFECT: Returning potentially unsafe path on error
            return filename;
        }
    }

    // DEFECT: Unsafe deserialization
    public static Object readObject(String filename) throws IOException, ClassNotFoundException {
        // DEFECT: Deserializing untrusted data
        FileInputStream fis = new FileInputStream(filename);
        ObjectInputStream ois = new ObjectInputStream(fis);

        // DEFECT: No validation of deserialized object
        Object obj = ois.readObject();

        // DEFECT: Not closing streams
        return obj;
    }

    // DEFECT: Method with side effects
    private static int fileOperationCount = 0;

    public static String readAndCount(String filename) throws IOException {
        // DEFECT: Side effect in a read method
        fileOperationCount++;

        return readFile(filename);
    }

    // DEFECT: Exposing mutable static state
    public static int getFileOperationCount() {
        return fileOperationCount;
    }
}
