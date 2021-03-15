package net.catstack.rsa.crypt

import net.catstack.rsa.models.PrivateKey
import net.catstack.rsa.models.PublicKey
import net.catstack.rsa.utils.BigInt
import net.catstack.rsa.utils.symbolsDictionary

class Decryptor(private val privateKey: PrivateKey) {
    fun decrypt(encryptedNumber: BigInt) = (encryptedNumber pow privateKey.d) % privateKey.mod
    fun decrypt(encryptedNumber: Int) = (BigInt(encryptedNumber) pow privateKey.d) % privateKey.mod

    fun decryptString(intArray: IntArray): String {
        val sb = StringBuilder()
        intArray.forEach {
            sb.append(symbolsDictionary[decrypt(it).toString().toInt()])
        }

        return sb.toString()
    }
}