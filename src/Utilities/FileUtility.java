package Utilities;

import com.sun.istack.internal.NotNull;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtility {
    public static byte[] getByteArrayOfFile(String path) {
        try {
            return Files.readAllBytes(new File(path).toPath());
        } catch (IOException e) {
            System.err.println("Could not get byte array of file!");
            e.printStackTrace();
            return null;
        }
    }

    public static boolean fileExists(String path) {
        File f = new File(path);
        return f.exists() && !f.isDirectory();
    }

    public static long getFileSize(@NotNull String path) {
        return new File(path).length();
    }

    public static  String calculateMD5(byte[] bytes) {
        System.out.print("Creating MD5 of a file (this might take a while)... ");
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5bytes = md.digest(bytes);
            String md5 = (new HexBinaryAdapter()).marshal(md5bytes);
            System.out.println(md5);
            return md5;
        } catch (NoSuchAlgorithmException e) {
            System.err.println("FAILURE");
            System.err.println("Could not generate MD5 hash of file!");
            e.printStackTrace();
            return null;
        }
    }

    public static String calculateMD5(String path) {
        byte[] bytesOfMessage = getByteArrayOfFile(path);
        return calculateMD5(bytesOfMessage);
    }
}
