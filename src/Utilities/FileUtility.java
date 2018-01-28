package Utilities;

import com.sun.istack.internal.NotNull;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtility {
    public static byte[] getByteArrayOfFile(File file) {
        return getByteArrayOfFile(file.toPath());
    }

    public static byte[] getByteArrayOfFile(Path path) {
        try {
            return Files.readAllBytes(path);
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
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5bytes = md.digest(bytes);
            String md5 = (new HexBinaryAdapter()).marshal(md5bytes);
            return md5;
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Could not generate MD5 hash!");
            e.printStackTrace();
            return null;
        }
    }

    public static String calculateMD5(String path) {
        byte[] bytesOfMessage = getByteArrayOfFile(Paths.get(path));
        return calculateMD5(bytesOfMessage);
    }

    public static String calculateFileMD5(File file) {
        String md5 = "";
        try {
            FileInputStream fis = new FileInputStream(file);
            md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return md5;
    }

    public static String getChecksum(String s) {
        return FileUtility.calculateMD5(s.getBytes());
    }
}
