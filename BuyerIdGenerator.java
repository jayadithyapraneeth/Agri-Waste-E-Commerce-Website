package com.agriwastetrade.site;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BuyerIdGenerator {
    
    private static final Map<String, Integer> generatedIds = new HashMap<>();
    private static final String SEPARATOR = "_";
    
    /**
     * Generates a unique user ID based on industry name and industry ID
     * @param industryName The buyer's industry name
     * @param industryId The industry ID
     * @return Unique user ID string
     */
    public static String generateUserId(String industryName, int industryId) {
        if(industryName == null || industryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Industry name cannot be null or empty");
        }
        
        // Create a base identifier using industry name and ID
        String baseIdentifier = normalizeString(industryName) + SEPARATOR + industryId;
        
        // Generate hash for uniqueness
        String hash = generateHash(baseIdentifier);
        
        // Combine with timestamp for additional uniqueness
        long timestamp = System.currentTimeMillis();
        return "BUYER_" + hash.substring(0, 8) + "_" + timestamp;
    }
    
    /**
     * Alternative method using UUID approach
     */
    public static String generateUserIdWithUuid(String industryName, int industryId) {
        if(industryName == null || industryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Industry name cannot be null or empty");
        }
        
        // Create a unique identifier from the input data
        String baseString = normalizeString(industryName) + SEPARATOR + industryId;
        
        // Use UUID with custom seed based on the inputs
        String uuidPart = generateUuidBasedOnInput(baseString);
        
        return "BUYER" + uuidPart.substring(0, 8).toUpperCase() + "_" + 
               Math.abs(industryId % 10000); // Add industry ID for traceability
    }
    
    /**
     * Generates a unique user ID based on industry name and industry ID with additional features
     */
    public static String generateAdvancedUserId(String industryName, int industryId) {
        if(industryName == null || industryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Industry name cannot be null or empty");
        }
        
        // Normalize the industry name
        String normalizedIndustry = normalizeString(industryName);
        
        // Generate a hash of the combination
        String combinedInput = normalizedIndustry + SEPARATOR + industryId;
        String hash = generateHash(combinedInput);
        
        // Create base components for the user ID
        String prefix = "BUYER";
        String randomPart = hash.substring(0, 6); // First 6 characters of hash
        
        return String.format("%s_%s_%d_%s", 
                           prefix, 
                           normalizedIndustry.substring(0, Math.min(4, normalizedIndustry.length())),
                           industryId,
                           randomPart);
    }
    
    /**
     * Normalizes string by removing special characters and converting to lowercase
     */
    private static String normalizeString(String input) {
        if(input == null) return "";
        return input.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    }
    
    /**
     * Generates SHA-256 hash of the input string
     */
    private static String generateHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }
    
    /**
     * Generates UUID-based identifier
     */
    private static String generateUuidBasedOnInput(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hashBytes = digest.digest(input.getBytes());
            
            // Convert to UUID-like format (simplified)
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xFF & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString().substring(0, 32); // First 32 chars
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating UUID", e);
        }
    }
    
    /**
     * Generates a unique sequential ID for the same industry to avoid duplicates
     */
    public static String generateSequentialUserId(String industryName, int industryId) {
        if(industryName == null || industryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Industry name cannot be null or empty");
        }
        
        String key = normalizeString(industryName) + "_" + industryId;
        Integer counter = generatedIds.getOrDefault(key, 0);
        generatedIds.put(key, counter + 1);
        
        return "BUYER_" + normalizeString(industryName).substring(0, Math.min(3, normalizeString(industryName).length())) 
               + industryId + "_" + String.format("%04d", counter);
    }
    
    /**
     * Example usage
     */
    public static void main(String[] args) {
        System.out.println("Generated User IDs:");
        
        // Test with different inputs
        System.out.println(generateUserId("Technology", 123));
        System.out.println(generateUserIdWithUuid("Technology", 456));//this is simple to note and remember
        System.out.println(generateUserIdWithUuid("Technology", 1234));
        System.out.println(generateAdvancedUserId("Technology", 789));
        System.out.println(generateSequentialUserId("Technology", 101));
        System.out.println(generateSequentialUserId("Technology", 101)); // Will be different
        System.out.println(generateSequentialUserId("Technology", 202));
        System.out.println(System.currentTimeMillis());
        
        // Example of invalid input handling
        try {
            generateUserId("", 123);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught exception: " + e.getMessage());
        }
    }
}

