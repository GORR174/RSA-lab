package net.catstack.rsa.crypt

import net.catstack.rsa.models.PrivateKey
import net.catstack.rsa.models.PublicKey
import net.catstack.rsa.utils.BigInt
import net.catstack.rsa.utils.primeNums
import java.lang.IllegalArgumentException

/***
 * Класс для генерации публичного и приватного ключа.
 * При создании объекта генерируется модуль ключей и функция Эйлера
 * @param p - простое число, необходимое для генерации ключей
 * @param q - простое число, отличное от q, необходимое для генерации ключей
 */
class KeysGenerator(p: BigInt, q: BigInt) {
    private val mod = p * q
    private val eulerFunction = (p - BigInt.ONE) * (q - BigInt.ONE)

    /***
     * @property e - открытая экспонента
     * При первом вызове параметра e он вычисляется, как взаимнопростое число с функцией Эйлера, меньшее самой функции Эйлера
     */
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

    /***
     * Функция для генерации открытого ключа с вычесленным модулем и открытой экспонентой e
     * @return открытый ключ
     */
    fun generatePublicKey() = PublicKey(mod, e)

    /***
     * Функция для генерации закрытого ключа с вычисленным модулем и закрытой экспонентой d
     * Экспонента d вычисляется как обратное число открытой экспоненте по модулю, равному значению функции эйлера
     * @return закрытый ключ
     */
    fun generatePrivateKey() = PrivateKey(mod, e modInverse eulerFunction)
}