package net.catstack.rsa.utils

import java.lang.IllegalArgumentException
import kotlin.math.absoluteValue

class BigInt {
    val nums: ArrayList<Byte> = ArrayList()
    val isNegative: Boolean

    constructor(numString: String) {
        var tempNumString = numString

        var isNegative: Boolean

        if (tempNumString.startsWith("-")) {
            tempNumString = tempNumString.substring(1)
            isNegative = true
        } else {
            isNegative = false
        }

        var trimZero = true
        tempNumString.forEach {
            if (trimZero && it == '0')
                return@forEach
            if (trimZero && it != '0')
                trimZero = false
            nums.add(it.toString().toByte())
        }

        if (nums.size == 0) {
            nums.add(0)
            isNegative = false
        }

        this.isNegative = isNegative
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
        var result = ZERO

        other.nums.forEachIndexed { index, num ->
            var buffer = timeByNumber(num.toInt())
            buffer = buffer.timesBy10(other.nums.lastIndex - index)
            result += buffer
        }

        val isNegative = this.isNegative != other.isNegative

        return BigInt(result.nums, isNegative)
    }

    fun findClosestDivider(num: BigInt, divider: BigInt): Pair<Byte, BigInt> {
        for (i in 9 downTo 1) {
            val result = divider * BigInt(i)
            if (result <= num) {
                return Pair(i.toByte(), result)
            }
        }

        throw IllegalArgumentException("first argument should be smaller than second argument")
    }

    fun timeByNumber(num: Int): BigInt {
        if (num < 0 || num > 9)
            throw IllegalArgumentException("Number must be in (0..9) range")
        if (num == 0)
            return ZERO
        if (num == 1)
            return this
        var result = this
        repeat(num - 1) {
            result += this
        }

        return result
    }

    operator fun div(other: BigInt): BigInt {
        if (this == ZERO)
            return ZERO
        if (other == ZERO)
            throw IllegalArgumentException("Divide by zero")
        if (other == ONE)
            return this
        if (other == NEGATIVE_ONE)
            return this.negate()
        val aNums = ArrayList<Byte>(nums)
        val divider = if (other.isNegative) other.negate() else other
        val result = ArrayList<Byte>()
        result.add(0)

        var divNum = ZERO
        while (aNums.size > 0) {
            val firstNum = aNums.removeFirst()
            divNum = BigInt(divNum.toString() + firstNum)
            if (divNum < divider) {
                result.add(0)
                continue
            }
            if (divNum == divider) {
                result.add(1)
                divNum = ZERO
            }
            if (divNum >= divider) {
                val closestDividerPair = findClosestDivider(divNum, divider)

                result.add(closestDividerPair.first)
                divNum -= closestDividerPair.second
            }
        }

        val resultIsNegative = isNegative != other.isNegative

        return BigInt("${if (resultIsNegative) "-" else ""}${result.joinToString(separator = "")}")
    }

    operator fun rem(other: BigInt) = this - other * (this / other)

    fun timesBy10(countOf10: Int): BigInt {
        val resultNums = ArrayList<Byte>(nums)
        repeat(countOf10) {
            resultNums.add(0)
        }

        return BigInt(resultNums, isNegative)
    }

    infix fun modInverse(module: BigInt): BigInt {
        if (module == ONE)
            return ZERO

        val m0 = module
        var m = module
        var y = ZERO
        var x = ONE

        var a = this

        while (a > ONE) {
            var q = a / m

            var t = m

            m = a % m
            a = t
            t = y

            y = x - q * y
            x = t
        }

        if (x < ZERO)
            x += m0

        return x
    }

    infix fun pow(other: BigInt): BigInt {
        var i = ONE
        var result = this
        while (i < other) {
            result *= this
            i += ONE
        }

        return result
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