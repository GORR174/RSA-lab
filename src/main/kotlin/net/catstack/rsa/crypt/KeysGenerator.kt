package net.catstack.rsa.crypt

import net.catstack.rsa.models.PrivateKey
import net.catstack.rsa.models.PublicKey
import net.catstack.rsa.utils.BigInt
import net.catstack.rsa.utils.primeNums
import java.lang.IllegalArgumentException

class KeysGenerator(val p: BigInt, val q: BigInt) {
    private val mod = p * q
    private val eulerFunction = (p - BigInt.ONE) * (q - BigInt.ONE)

    private val e: BigInt by lazy {
        var tempE: BigInt = BigInt.ZERO
        primeNums.forEach {
            if (it < eulerFunction && eulerFunction % it != BigInt.ZERO) {
                tempE = it
                return@lazy tempE
            }
        }

        throw IllegalArgumentException("Try other p and q values")
    }

    fun generatePublicKey() = PublicKey(mod, e)

    fun generatePrivateKey() = PrivateKey(mod, e modInverse eulerFunction)
}