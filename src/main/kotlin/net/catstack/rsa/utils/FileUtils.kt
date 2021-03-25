package net.catstack.rsa.utils

import java.io.File
import java.nio.ByteBuffer

/***
 * Функция-расширение для класса File, записывающая массив чисел в файл, как массив байт
 * @param array - массив чисел на запись
 */
fun File.writeIntArrayToFile(array: IntArray) {
    val byteBuffer = ByteBuffer.allocate(array.size * 4)

    val intBuffer = byteBuffer.asIntBuffer()
    array.forEach(intBuffer::put)

    this.writeBytes(byteBuffer.array())
}

/***
 * Функция-расширение для класса File, преобразующая массив байт из файла в массив чисел
 * @return массив чисел
 */
fun File.readIntArrayFromFile(): IntArray {
    val intBuffer = ByteBuffer.wrap(this.readBytes()).asIntBuffer()
    val intArray = IntArray(intBuffer.capacity())

    for (i in 0 until intBuffer.capacity())
        intArray[i] = intBuffer[i]

    return intArray
}