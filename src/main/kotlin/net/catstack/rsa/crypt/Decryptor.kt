package net.catstack.rsa.crypt

import net.catstack.rsa.models.PrivateKey
import net.catstack.rsa.models.PublicKey
import net.catstack.rsa.utils.BigInt
import net.catstack.rsa.utils.symbolsDictionary

class Decryptor(private val privateKey: PrivateKey) {
    private var lastCount = 0

    fun decrypt(encryptedNumber: BigInt) = (encryptedNumber pow privateKey.d) % privateKey.mod
    fun decrypt(encryptedNumber: Int) = (BigInt(encryptedNumber) pow privateKey.d) % privateKey.mod

    fun decryptString(intArray: IntArray, logProgress: Boolean = false): String {
        lastCount = 0
        print("[--------------------]\r")
        val sb = StringBuilder()
        intArray.forEach {
            sb.append(symbolsDictionary[decrypt(it).toString().toInt()])
            if (logProgress) {
                val progress = sb.length / intArray.size.toFloat()

                val fillCount = (progress * 20).toInt()
                if (fillCount != lastCount) {
                    print("[")
                    for (i in 1..20) {
                        if (i <= fillCount)
                            print("#")
                        else
                            print("-")
                    }
                    print("]\r")
                    lastCount = fillCount
                }
            }
        }

        println("[--------100%--------]")

        return sb.toString()
    }
}