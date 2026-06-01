package co.edu.uptc.security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordEncoder {
    public static String encode(String password) {

        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean matches(String rawPassword,
                                  String hashedPassword) {

        return BCrypt.checkpw(rawPassword, hashedPassword);
    }
}
