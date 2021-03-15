package net.catstack.rsa

import net.catstack.rsa.crypt.Decryptor
import net.catstack.rsa.crypt.Encryptor
import net.catstack.rsa.crypt.KeysGenerator
import net.catstack.rsa.utils.BigInt
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    println((measureTimeMillis {
        val keysGenerator = KeysGenerator(
            BigInt("31"),
            BigInt("29"),
        )

        val publicKey = keysGenerator.generatePublicKey()
        val privateKey = keysGenerator.generatePrivateKey()

        println(publicKey)
        println(privateKey)

        val encryptor = Encryptor(publicKey)
        val decryptor = Decryptor(privateKey)

        val crypted = encryptor.encrypt(135)

        println(crypted)

        val decrypted = decryptor.decrypt(crypted)

        println(decrypted)
    } / 1000f).toString() + "s")
}