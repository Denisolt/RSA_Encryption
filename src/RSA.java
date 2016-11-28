
/**
 * ***********************************************************************
 *  Compilation:  javac RSA.java
 *  Execution:    java RSA n
 *
 *  Generate an N-bit public and private RSA key and use to encrypt
 *  and decrypt a random message of size n/2.
 *
 *  % java RSA 50
 *  public  = 65537
 *  private = 553699199426609
 *  modulus = 825641896390631
 *  message   = 48194775244950
 *  encrpyted = 321340212160104
 *  decrypted = 48194775244950
 *
 *  Known bugs (not addressed for simplicity)
 *  -----------------------------------------
 *  - It could be the case that the message >= modulus. To avoid, use
 *    a do-while loop to generate key until modulus happen to be exactly N bits.
 *
 *  - It's possible that gcd(phi, e) != 1 in which case
 *    the key generation fails. This will only happen if phi is a
 *    multiple of 65537. To avoid, use a do-while loop to generate
 *    keys until the gcd is 1.
 *
 ************************************************************************
 */

import java.math.BigInteger;
import java.security.SecureRandom;

public class RSA {

    private final static BigInteger one = new BigInteger("1");
    private final static SecureRandom random = new SecureRandom();
        private final static BigInteger two = new BigInteger("4");


    private BigInteger privateKey;
    private BigInteger e;
    private BigInteger modulus;

    // generate an N-bit (roughly) public and private key
    RSA(int N) 
    {
        BigInteger p;
        BigInteger q;
        BigInteger phi;

        e = new BigInteger("65537");     // common value in practice = 2^16 + 1	   

        while (true) 
        {
            p = BigInteger.probablePrime(N / 2, random);
            q = BigInteger.probablePrime(N / 2, random);
            phi = (p.subtract(one)).multiply(q.subtract(one));
            if (phi.gcd(e).equals(one)) 
            {
                break;
            }
            System.out.println("GCD");
        }
        modulus = p.multiply(q);
        privateKey = e.modInverse(phi);
    }

    BigInteger encrypt(BigInteger message) 
    {
      return message.modPow(e, modulus);
    }

    BigInteger decrypt(BigInteger encrypted) 
    {
        return encrypted.modPow(privateKey, modulus);
    }

    // This method takes in input a ciphertext that encrypts a message m, and an unencrypted integer factor. 
    // It returns the encryption of (m * factor)
    BigInteger multiply(BigInteger encrypted, BigInteger factor) 
    {
        return factor.modPow(e, modulus).multiply(encrypted).mod(modulus);
    }

    public String toString() 
    {
        String s = "";
        s += "public  = " + e + "\n";
        s += "private = " + privateKey + "\n";
        s += "modulus = " + modulus;
        return s;
    }

    public static void main(String[] args) 
    {
        int N = Integer.parseInt(args[0]);
        RSA key = null;
        key = new RSA(N);

        System.out.println(key);

        // create random message, encrypt and decrypt
        BigInteger message = new BigInteger(N / 2, random);

        BigInteger encrypt = key.encrypt(message);
        BigInteger decrypt = key.decrypt(encrypt);
        System.out.println("message   = " + message);
        System.out.println("encrpyted = " + encrypt);
        System.out.println("decrypted = " + decrypt);
        System.out.println("Is the decrypted message the same as the original message? => " + message.equals(decrypt));

        BigInteger modified = key.multiply(encrypt, new BigInteger("4"));
        BigInteger decrypt_modified = key.decrypt(modified);
        System.out.println("modified   = " + decrypt_modified);

    }
}
