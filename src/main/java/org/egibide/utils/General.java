package org.egibide.utils;

import org.egibide.Modelo.Usuario;

import javax.crypto.*;
import java.io.IOException;
import java.security.*;

public class General {

    // Para el password
    public static byte[] elHash(String algoritmos, String texto) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algoritmos);
        md.update(texto.getBytes());
        return md.digest();
    }

    // Para claves asimetricas
    public static KeyPair generarParClaves(String algoritmo) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algoritmo);
        keyPairGenerator.initialize(2048); // Tamaño de la clave de 2048 bits
        return keyPairGenerator.generateKeyPair();
    }

    public static byte[] cifrarConClavePublica(byte[] mensaje, PublicKey clavePublica, String algoritmo) throws Exception {
        Cipher cipher = Cipher.getInstance(algoritmo);
        cipher.init(Cipher.ENCRYPT_MODE, clavePublica);
        return cipher.doFinal(mensaje); // Devuelve los bytes cifrados
    }

    public static byte[] descifrarConClavePrivada(byte[] mensajeCifrado, PrivateKey clavePrivada, String algoritmo) throws Exception {
        Cipher cipher = Cipher.getInstance(algoritmo);
        cipher.init(Cipher.DECRYPT_MODE, clavePrivada);
        return cipher.doFinal(mensajeCifrado);
        //return new String(mensajeDescifrado);
    }

    //Para clave simetrica
    public static byte[] cifrarObjeto(byte[] objeto, SecretKey secretKey , String algoritmo) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(algoritmo);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(objeto);

   }
    public static byte[] descifrarObjeto(byte[] objeto, SecretKey secretKey, String algoritmo) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(algoritmo);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(objeto);
    }

}
