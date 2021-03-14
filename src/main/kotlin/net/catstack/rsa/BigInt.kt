package net.catstack.rsa

import java.lang.IllegalArgumentException
import kotlin.math.absoluteValue

class BigInt {
    val nums: ArrayList<Byte> = ArrayList()
    val isNegative: Boolean

    constructor(numString: String) {
        var tempNumString = numString

        if (tempNumString.startsWith("-")) {
            tempNumString = tempNumString.substring(1)
            isNegative = numString.length != 2 || numString[1] != '0'
        } else {
            isNegative = false
        }

        var trimZero = tempNumString.length > 1
        tempNumString.forEach {
            if (trimZero && it == '0')
                return@forEach
            if (trimZero && it != '0')
                trimZero = false
            nums.add(it.toString().toByte())
        }
    }

    constructor(num: Int) {
        if (num == 0) {
            nums.add(0)
            isNegative = false
            return
        }

        isNegative = num < 0

        var tempNum = num.absoluteValue

        while (tempNum > 0) {
            nums.add(0, (tempNum % 10).toByte())
            tempNum /= 10
        }
    }

    constructor(numArray: ArrayList<Byte>, isNegative: Boolean = false) {
        this.nums.clear()
        this.nums.addAll(numArray)

        this.isNegative = isNegative
    }

    companion object {
        val ZERO = BigInt("0")
        val ONE = BigInt("1")
        val NEGATIVE_ONE = BigInt("-1")
    }

    operator fun compareTo(other: BigInt): Int {
        if (isNegative && !other.isNegative)
            return -1
        if (!isNegative && other.isNegative)
            return 1
        if (nums.size > other.nums.size)
            return if (!isNegative) 1 else -1
        if (nums.size < other.nums.size)
            return if (!isNegative) -1 else 1
        nums.forEachIndexed { i, num ->
            if (num > other.nums[i])
                return if (!isNegative) 1 else -1
            else if (num < other.nums[i])
                return if (!isNegative) -1 else 1
        }

        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (other is BigInt)
            return compareTo(other) == 0
        return super.equals(other)
    }

    fun negate() = BigInt(nums, !isNegative)

    operator fun plus(other: BigInt): BigInt {
        val aNums = ArrayList<Byte>(nums)
        val bNums = ArrayList<Byte>(other.nums)
        while (aNums.size > bNums.size)
            bNums.add(0, 0)
        while (bNums.size > aNums.size)
            aNums.add(0, 0)

        var index = aNums.lastIndex
        val result = ArrayList<Byte>()
        val resultIsNegative = (isNegative && other.isNegative) || (isNegative && this.negate() > other) || (!isNegative && this < other.negate())
        if (isNegative == other.isNegative) {
            var s = 0
            while (index >= 0) {
                val sum = (aNums[index] + bNums[index] + s).toByte()
                val lastNum = (sum % 10).toByte()
                s = sum / 10
                result.add(0, lastNum)
                index--
            }

            if (s > 0) {
                result.add(0, (s % 10).toByte())
            }
        } else {
            val moduleA = if (this.isNegative) this.negate() else this
            val moduleB = if (other.isNegative) other.negate() else other

            if (moduleA == moduleB)
                return ZERO

            val largest = if (moduleA > moduleB) aNums else bNums
            val smallest = if (moduleA <= moduleB) aNums else bNums

            var needToSubtractOne = false
            while (index >= 0) {
                val clearDiff = largest[index] - smallest[index]
                var diff = if (needToSubtractOne) clearDiff - 1 else clearDiff
                needToSubtractOne = false

                if (diff < 0) {
                    diff += 10
                    needToSubtractOne = true
                }

                result.add(0, diff.toByte())
                index--
            }
        }

        return BigInt("${if (resultIsNegative) "-" else ""}${result.joinToString(separator = "")}")
    }

    operator fun minus(other: BigInt) = this + other.negate()

    operator fun times(other: BigInt): BigInt {
        var result = BigInt.ZERO

        other.nums.forEachIndexed { index, num ->
            var buffer = timeByNumber(num.toInt())
            buffer = buffer.timesBy10(other.nums.lastIndex - index)
            result += buffer
        }

        val isNegative = this.isNegative != other.isNegative

        return BigInt(result.nums, isNegative)
    }

    fun timeByNumber(num: Int): BigInt {
        if (num < 0 || num > 9)
            throw IllegalArgumentException("Number must be in (0..9) range")
        if (num == 0)
            return BigInt.ZERO
        if (num == 1)
            return this
        var result = this
        repeat(num - 1) {
            result += this
        }

        return result
    }

    fun timesBy10(countOf10: Int): BigInt {
        val resultNums = ArrayList<Byte>(nums)
        repeat(countOf10) {
            resultNums.add(0)
        }

        return BigInt(resultNums, isNegative)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        if (isNegative)
            sb.append("-")
        nums.forEach(sb::append)
        return sb.toString()
    }

    override fun hashCode(): Int {
        var result = nums.hashCode()
        result = 31 * result + isNegative.hashCode()
        return result
    }
}