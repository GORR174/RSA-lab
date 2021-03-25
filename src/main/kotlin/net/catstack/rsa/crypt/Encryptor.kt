package net.catstack.rsa.crypt

import net.catstack.rsa.models.PublicKey
import net.catstack.rsa.utils.BigInt
import net.catstack.rsa.utils.symbolsDictionary

/***
 * Класс шифратор
 * @property publicKey - публичный ключ, необходимый для шифрования
 */
class Encryptor(private val publicKey: PublicKey) {
    /***
     * Шифрует число
     * @param numToCrypt - число для шифрования
     * @return зашифрованное число
     */
    fun encrypt(numToCrypt: BigInt) = (numToCrypt pow publicKey.e) % publicKey.mod

    /***
     * Шифрует число
     * @param numToCrypt - число для шифрования
     * @return зашифрованное число
     */
    fun encrypt(numToCrypt: Int) = (BigInt(numToCrypt) pow publicKey.e) % publicKey.mod

    /***
     * Шифрует строку
     * @param str - строка для шифрования
     * @return возвращает список чисел, где каждое число - зашифрованный символ
     */
    fun encryptString(str: String): IntArray {
        val intArray = IntArray(str.length)

        str.forEachIndexed { index, char ->
            intArray[index] = encrypt(symbolsDictionary.indexOf(char)).toString().toInt()
        }

        return intArray
    }
}