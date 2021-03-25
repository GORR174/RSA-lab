package net.catstack.rsa.crypt

import net.catstack.rsa.models.PrivateKey
import net.catstack.rsa.utils.BigInt
import net.catstack.rsa.utils.symbolsDictionary

/***
 * Класс для дешифрования
 * @property privateKey - приватный ключ, необходимый для шифровани
 */
class Decryptor(private val privateKey: PrivateKey) {

    /***
     * Функция расшифровки зашифрованного числа
     * @param encryptedNumber - зашифрованное число
     * @return расшифрованное число
     */
    fun decrypt(encryptedNumber: BigInt) = (encryptedNumber pow privateKey.d) % privateKey.mod

    /***
     * Функция расшифровки зашифрованного числа
     * @param encryptedNumber - зашифрованное число
     * @return расшифрованное число
     */
    fun decrypt(encryptedNumber: Int) = (BigInt(encryptedNumber) pow privateKey.d) % privateKey.mod

    /***
     * Функция расшифровки зашифрованного текста
     * @param intArray - зашифрованный текст в формате массива зашифрованных чисел
     * @param logProgress - при true создаёт динамическую полосу процесса расшифровки
     * @return расшифрованный текст
     */
    fun decryptString(intArray: IntArray, logProgress: Boolean = false): String {
        var lastCount = 0
        if (logProgress)
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

        if (logProgress)
            println("[--------100%--------]")

        return sb.toString()
    }
}