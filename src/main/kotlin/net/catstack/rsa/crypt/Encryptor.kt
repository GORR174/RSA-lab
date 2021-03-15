package net.catstack.rsa.crypt

import net.catstack.rsa.models.PublicKey
import net.catstack.rsa.utils.BigInt
import net.catstack.rsa.utils.symbolsDictionary

class Encryptor(private val publicKey: PublicKey) {
    fun encrypt(numToCrypt: BigInt) = (numToCrypt pow publicKey.e) % publicKey.mod
    fun encrypt(numToCrypt: Int) = (BigInt(numToCrypt) pow publicKey.e) % publicKey.mod

    fun encryptString(str: String): IntArray {
        val intArray = IntArray(str.length)

        str.forEachIndexed { index, char ->
            intArray[index] = encrypt(symbolsDictionary.indexOf(char)).toString().toInt()
        }

        return intArray
    }
}