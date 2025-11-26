package edu.univ.erp.util;

import org.mindrot.jbcrypt.BCrypt;
/**
 * Utility class for generating and verifying BCrypt hashes.
 */

public class HashGenerator {
    public static void main(String[] args) {
        String password = "erp";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println(hashedPassword);
    }
}
