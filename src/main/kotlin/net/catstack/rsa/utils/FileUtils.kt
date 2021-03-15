package net.catstack.rsa.utils

import java.io.File
import java.nio.ByteBuffer

fun File.writeIntArrayToFile(array: IntArray) {
    val byteBuffer = ByteBuffer.allocate(array.size * 4)

    val intBuffer = byteBuffer.asIntBuffer()
    array.forEach(intBuffer::put)

    this.writeBytes(byteBuffer.array())
}

fun File.readIntArrayFromFile(): IntArray {
    val intBuffer = ByteBuffer.wrap(this.readBytes()).asIntBuffer()
    val intArray = IntArray(intBuffer.capacity())

    for (i in 0 until intBuffer.capacity())
        intArray[i] = intBuffer[i]

    return intArray
}