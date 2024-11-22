package kr.brain.our_app.idsha;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IDGenerator {
    public static String generateId(String code){
        try{
            String currentTime = getCurrentTime();
            String input = code + currentTime;

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        }catch (NoSuchAlgorithmException e){
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    private static String bytesToHex(byte[] hashBytes){
        StringBuilder retString = new StringBuilder();
        for(byte b : hashBytes){
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() == 1)retString.append('0');
            retString.append(hex);
        }
        return retString.toString();
    }
    private static String getCurrentTime(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        return now.format(formatter);
    }
}
