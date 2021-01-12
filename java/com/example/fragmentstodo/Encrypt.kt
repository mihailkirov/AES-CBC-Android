package com.example.fragmentstodo

import java.lang.Exception
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class Encrypt {

    private var ivSpec:ByteArray? = null

    private fun generateSalt(): ByteArray {
        val random = SecureRandom() // cryptographically strong random number generator.
        var salt =  ByteArray(256)
        random.nextBytes(salt) // placing 256 tandom bytes in salt
        return salt
    }

    /**
     *@param: password as charArray so it can be mutable and sensitive information therefore be
     * overwritten
     */

    private fun generateKey(password:CharArray, salt:ByteArray): SecretKeySpec {


        val pbKeySpec = PBEKeySpec(password, salt, 1324, 256)
        // iteration count for hard bruteforce attack

        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val keyBytes = secretKeyFactory.generateSecret(pbKeySpec).encoded  // generation of the key as bytes
        return SecretKeySpec(keyBytes, "AES") // Wrapped the raw ByteArray into a SecretKeySpec object.
    }

    private fun generateIV(): ByteArray? {
        val ivRandom = SecureRandom()
        ivSpec = ByteArray(16)
        ivRandom.nextBytes(ivSpec)
        return ivSpec
    }

    /**
     * Function encrypting the data passed as parameter
     * @param dataToEnctypt - data to be encrypted
     * Choosing AES Chain Block enctyption with PKCS7Padding standard (added before encryption)
     * @return a byte array of cypher text
     * */

    fun ecnrypt(dataToEnctypt:ByteArray, password:CharArray, map:HashMap<String, ByteArray>):ByteArray {
        val salt = generateSalt()
        val iv = generateIV()
        val keySpec = generateKey(password, salt) // it'll be regenerated when decrypting
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding") // 1
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(iv) )
        map["salt"] = salt
        map["iv"] = iv!!
        return cipher.doFinal(dataToEnctypt)

    }

    fun decrypt(encrypted:ByteArray, password: CharArray, salt: ByteArray, iv:ByteArray): ByteArray? {
        try {
            /* Regenerate key for decryption */
            val pbKeySpec = PBEKeySpec(password, salt, 1324, 256)
            val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val keyBytes = secretKeyFactory.generateSecret(pbKeySpec).encoded
            val keySpec = SecretKeySpec(keyBytes, "AES")
            // Decryption part
            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(Cipher.DECRYPT_MODE, keySpec, IvParameterSpec(iv))
            return cipher.doFinal(encrypted)
        }catch (e:Exception){
            println("Error"+ e.message)
            return null;
        }

    }

}


