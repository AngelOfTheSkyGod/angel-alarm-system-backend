package com.angelalarmsystembackend.angel_alarm_system.utils;
import com.angelalarmsystembackend.angel_alarm_system.model.DeviceClientData;
import com.angelalarmsystembackend.angel_alarm_system.service.DeviceService;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.regex.Pattern;

public class AccountUtils {

    // Regular expression to enforce the password rules
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])" +          // At least one digit
                    "(?=.*[a-z])" +           // At least one lowercase letter
                    "(?=.*[A-Z])" +           // At least one uppercase letter
                    "(?=.*[@#$%^&+=!])" +     // At least one special character
                    "(?=\\S+$)" +             // No whitespace allowed
                    ".{8,}$";                 // At least 8 characters

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public static boolean isStrongPassword(String password) {
        if (password == null) {
            return false;
        }
        return pattern.matcher(password).matches();
    }

    // Method to generate a salt
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    // Method to hash a password with the salt
    public static String hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }

    // Method to store salt and hash
    public static String[] storePassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = generateSalt();
        String hash = hashPassword(password, salt);
        String saltStr = Base64.getEncoder().encodeToString(salt);
        return new String[] { saltStr, hash };
    }

    // Method to verify the password
    public static boolean verifyPassword(String password, String storedSalt, String storedHash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = Base64.getDecoder().decode(storedSalt);
        String hash = hashPassword(password, salt);
        return hash.equals(storedHash);
    }

    public static boolean isAuthenticated(String userIdentifier, String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (DeviceService.clientToMachineMap.get(userIdentifier) != null && !DeviceService.clientToMachineMap.get(userIdentifier).getDeviceName().equalsIgnoreCase(username)){
            System.err.println("user is connected to a device already.");
            return false;
        }
        DeviceClientData deviceClient = DeviceService.deviceNameToDeviceClientData.get(username);
        return (verifyPassword(password, deviceClient.getSalt(), deviceClient.getPasswordHash()));
    }

}