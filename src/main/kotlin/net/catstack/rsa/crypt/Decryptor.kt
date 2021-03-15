package net.catstack.rsa.crypt

import net.catstack.rsa.models.PrivateKey
import net.catstack.rsa.models.PublicKey
import net.catstack.rsa.utils.BigInt

class Decryptor(private val privateKey: PrivateKey) {
    fun decrypt(encryptedNumber: BigInt) = (encryptedNumber pow privateKey.d) % privateKey.mod
    fun decrypt(encryptedNumber: Int) = (BigInt(encryptedNumber) pow privateKey.d) % privateKey.mod
}