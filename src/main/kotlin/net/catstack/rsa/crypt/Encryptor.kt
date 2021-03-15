package net.catstack.rsa.crypt

import net.catstack.rsa.models.PublicKey
import net.catstack.rsa.utils.BigInt

class Encryptor(private val publicKey: PublicKey) {
    fun encrypt(numToCrypt: BigInt) = (numToCrypt pow publicKey.e) % publicKey.mod
    fun encrypt(numToCrypt: Int) = (BigInt(numToCrypt) pow publicKey.e) % publicKey.mod
}