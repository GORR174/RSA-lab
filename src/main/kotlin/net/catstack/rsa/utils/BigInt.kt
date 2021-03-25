package net.catstack.rsa.utils

import java.lang.IllegalArgumentException
import kotlin.math.absoluteValue

/***
 * Класс, необходимый для работы с большими числами
 */
class BigInt {
    val nums: ArrayList<Byte> = ArrayList()
    val isNegative: Boolean

    /***
     * Конструктор, создающий BigInt из строкового представления числа
     * @param numString - строка, которая будет преобразована в BigInt
     */
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

    /***
     * Конструктор, создающий BigInt из числа в формате Int
     * @param num - число, из которого будет создан BigInt
     */
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

    /***
     * Конструктор, создающий BigInt из списка цифр
     * @param numArray - список цифр
     * @param isNegative - флаг, подчёркивающий положительность или отрицательность числа
     */
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

    /***
     * Переопределения операторов сравнения (>, >=, <, <=, ==, !=)
     * @param other - число, с которым сравниваем число, на котором вызвали метод
     * @return возвращает отрицательное число, если a < b, положительное число, если a > b, 0 если числа равны
     */
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

    /***
     * Переопределение метода для сравнения чисел по значению
     * @param other - число, с которым сравниваем число, на котором вызвали метод
     * @return возвращает true, если числа равны
     */
    override fun equals(other: Any?): Boolean {
        if (other is BigInt)
            return compareTo(other) == 0
        return super.equals(other)
    }

    /***
     * Функция для изменения знака числа
     * @return число с противоположным знаком
     */
    fun negate() = BigInt(nums, !isNegative)

    /***
     * Оператор сложения двух чисел
     * @param other - число, которое складывается с числом, на котором вызвали метод
     * @return сумма двух чисел
     */
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

    /***
     * Оператор разности двух чисел
     * @param other - вычитаемое число
     * @return разность двух чисел
     */
    operator fun minus(other: BigInt) = this + other.negate()

    /***
     * Оператор умножения двух чисел
     * @param other - число, на которое умножаем число, у которого вызвали метод
     * @return произведение двух чисел
     */
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

    /***
     * Функция умножения числа на цифру
     * @param num - цифра для умножения
     * @return результат произведения на цифру
     * @exception IllegalArgumentException - ошибка выбрасывается, если параметр num не в диапазоне от 0 до 9
     */
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

    /***
     * Функция умножения числа на степень десятки
     * @param countOf10 степень десятки
     * @return результат умножения
     */
    fun timesBy10(countOf10: Int): BigInt {
        val resultNums = ArrayList<Byte>(nums)
        repeat(countOf10) {
            resultNums.add(0)
        }

        return BigInt(resultNums, isNegative)
    }

    /***
     * Функция деления на число
     * @param other - делитель
     * @return результат деления
     * @exception IllegalArgumentException - выкидывается при делении на 0
     */
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

    /***
     * Функция, возвращающая ближайшую цифру, при умножении на которую будет наибольший делитель для числа
     * @param num - число, к которому мы ищем наибольший делитель
     * @param divider - делитель
     * @return возвращает пару чисел, где первое число - цифра, на которую нужно умножить делитель, а второе число - результат умножения
     * @exception IllegalArgumentException - неверные аргументы
     */
    fun findClosestDivider(num: BigInt, divider: BigInt): Pair<Byte, BigInt> {
        for (i in 9 downTo 1) {
            val result = divider * BigInt(i)
            if (result <= num) {
                return Pair(i.toByte(), result)
            }
        }

        throw IllegalArgumentException("first argument should be smaller than second argument")
    }

    /***
     * Функция, вычисляющая остаток от деления
     * @param other - делитель
     * @return остаток от деления
     */
    operator fun rem(other: BigInt) = this - other * (this / other)

    /***
     * Функция, возвращающая обратное по модулю число. Функция использует расширенный алгоритм Евклида.
     * @param module - модуль, по которому необходимо найти обратное число
     */
    infix fun modInverse(module: BigInt): BigInt {
        if (module == ONE)
            return ZERO

        var m = module
        var y = ZERO
        var x = ONE

        var a = this

        while (a > ONE) {
            val q = a / m

            var t = m

            m = a % m
            a = t
            t = y

            y = x - q * y
            x = t
        }

        if (x < ZERO)
            x += module

        return x
    }

    /***
     * Функция возведения в степень
     * @param other - степень числа
     * @return результат возведения в степень
     */
    infix fun pow(other: BigInt): BigInt {
        var i = ZERO
        var result = ONE
        while (i < other) {
            result *= this
            i += ONE
        }

        return result
    }

    /***
     * Переопределяем метод toString для превращения объекта в строку (например для дальнейшего вывода числа в консоль)
     * @return строковое представление числа
     */
    override fun toString(): String {
        val sb = StringBuilder()
        if (isNegative)
            sb.append("-")
        nums.forEach(sb::append)
        return sb.toString()
    }

    /***
     * Функция для вычисления хеша объекта (для одинаковых объектов должен быть одинаковый хеш код)
     * @return хеш код числа
     */
    override fun hashCode(): Int {
        var result = nums.hashCode()
        result = 31 * result + isNegative.hashCode()
        return result
    }
}