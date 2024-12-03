package org.egibide.utils;

import org.egibide.Modelo.Usuario;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class General {

    public static byte[] elHash(String algoritmos, String texto) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algoritmos);
        md.update(texto.getBytes());
        return md.digest();
    }
}
