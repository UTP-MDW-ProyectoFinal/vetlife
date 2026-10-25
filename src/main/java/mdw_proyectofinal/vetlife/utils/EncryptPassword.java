package mdw_proyectofinal.vetlife.utils;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class EncryptPassword {
  static Argon2 argon2 = Argon2Factory.create();

  public static String encrypt(String password) {
    char[] charPassword = password.toCharArray();
    String hash;

    try {
      hash = argon2.hash(2, 19456, 1, charPassword);
    } finally {
      argon2.wipeArray(charPassword);
    }

    return hash;
  }

  public static Boolean test(String hash, String password) {
    char[] charPassword = password.toCharArray();
    Boolean result;

    try {
      result = argon2.verify(hash, charPassword);

    } finally {
      argon2.wipeArray(charPassword);
    }

    return result;
  }
}
