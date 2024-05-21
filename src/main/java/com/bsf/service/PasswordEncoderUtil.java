package com.bsf.service;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordEncoderUtil {

    public static String encode(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
