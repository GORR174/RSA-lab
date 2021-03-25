package net.catstack.rsa.application

import net.catstack.rsa.crypt.Decryptor
import net.catstack.rsa.crypt.Encryptor
import net.catstack.rsa.crypt.KeysGenerator
import net.catstack.rsa.utils.BigInt
import net.catstack.rsa.utils.readIntArrayFromFile
import net.catstack.rsa.utils.writeIntArrayToFile
import java.io.File
import java.io.FileFilter
import java.util.*
import kotlin.system.measureTimeMillis

class ApplicationMain : Application {
    private var shouldExit = false

    val keysGenerator = KeysGenerator(
        BigInt("23"),
        BigInt("19"),
    )

    val publicKey = keysGenerator.generatePublicKey()
    val privateKey = keysGenerator.generatePrivateKey()

    val encryptor = Encryptor(publicKey)
    val decryptor = Decryptor(privateKey)

    val scanner = Scanner(System.`in`, "windows-1251")

    /***
     * Метод, который вызывается при запуске приложения (вызывается из метода main)
     */
    override fun run() {
        println("+------------------------------------+")
        println("|           RSA шифрование           |")
        println("| Программу сделал Бородин Владислав |")
        println("+------------------------------------+\n")

        while (!shouldExit) {
            println("Выберите действие:")
            println("[1] - зашифровать число")
            println("[2] - зашифровать текст")
            println("[3] - зашифровать текстовый файл")
            println("[4] - расшифровать зашифрованный файл")
            println("[0] - выход")

            print("Введите число: ")

            val input = scanner.nextLine()

            when (input) {
                "1" -> cryptNumber()
                "2" -> cryptText()
                "3" -> cryptTextFile()
                "4" -> decryptTextFile()
                "0" -> shouldExit = true
                else -> println("Ошибка ввода!")
            }

            println("+------------------------------------+")
        }

        println("Спасибо за использование!")
    }

    /***
     * Меню шифрования числа
     */
    private fun cryptNumber() {
        println("+------------------------------------+")
        println("|          Зашифровать число         |")
        println("+------------------------------------+")

        print("Введите число, которое вы хотите зашифровать: ")
        val num = scanner.nextLine().toInt()
        println("Шифрование...")

        val encrypted: BigInt
        val encryptionTime = measureTimeMillis { encrypted = encryptor.encrypt(num) } / 1000f

        val decrypted: BigInt
        val decryptionTime = measureTimeMillis { decrypted = decryptor.decrypt(encrypted) } / 1000f

        println("Зашифрованное число: $encrypted (${encryptionTime}с.)")
        println("Расшифрованное число: $decrypted (${decryptionTime}с.)")

        println("\nДля продолжения нажмите Enter...")
        scanner.nextLine()
    }

    /***
     * Меню шифрования текста
     */
    private fun cryptText() {
        println("+------------------------------------+")
        println("|          Зашифровать текст         |")
        println("+------------------------------------+")

        print("Введите строку, которую вы хотите зашифровать: ")
        val text = scanner.nextLine()
        println("Шифрование...")

        val encrypted: IntArray
        val encryptionTime = measureTimeMillis { encrypted = encryptor.encryptString(text) } / 1000f

        val decrypted: String
        val decryptionTime = measureTimeMillis { decrypted = decryptor.decryptString(encrypted, true) } / 1000f

        println("Зашифрованная строка: ${encrypted.joinToString(prefix = "[", postfix = "]")} (${encryptionTime}с.)")
        println("Расшифрованная строка: $decrypted (${decryptionTime}с.)")

        println("\nДля продолжения нажмите Enter...")
        scanner.nextLine()
    }

    /***
     * Меню шифрования файла
     */
    private fun cryptTextFile() {
        println("+------------------------------------+")
        println("|          Зашифровать файл          |")
        println("+------------------------------------+")

        println("Выберите файл (файлы должны лежать в папке in с расширением .txt):")

        val inFolder = File("in")
        inFolder.mkdir()
        val outFolder = File("out")
        outFolder.mkdir()

        val files = inFolder.listFiles(FileFilter { it.extension == "txt" })!!

        files.forEachIndexed { index, file ->
            println("[${index + 1}] - ${file.name}")
        }

        val inFile = files[scanner.nextLine().toInt() - 1]

        val text = inFile.readText()

        println("Шифрование...")

        val encrypted: IntArray
        val encryptionTime = measureTimeMillis { encrypted = encryptor.encryptString(text) } / 1000f

        val outFile = File("out/${inFile.nameWithoutExtension}.cpt")
        outFile.createNewFile()
        outFile.writeIntArrayToFile(encrypted)

        println("Зашифрованный файл: out/${outFile.name} (${encryptionTime}с.)")

        println("\nДля продолжения нажмите Enter...")
        scanner.nextLine()
    }

    /***
     * Меню расшифровки файла
     */
    fun decryptTextFile() {
        println("+------------------------------------+")
        println("|         Расшифровать файл          |")
        println("+------------------------------------+")

        println("Выберите файл (файлы должны лежать в папке in с расширением .cpt):")

        val inFolder = File("in")
        inFolder.mkdir()
        val outFolder = File("out")
        outFolder.mkdir()

        val files = inFolder.listFiles(FileFilter { it.extension == "cpt" })!!

        files.forEachIndexed { index, file ->
            println("[${index + 1}] - ${file.name}")
        }

        val inFile = files[scanner.nextLine().toInt() - 1]

        val text = inFile.readText()

        println("Дешифрование...")

        val encrypted = inFile.readIntArrayFromFile()

        val decrypted: String
        val encryptionTime = measureTimeMillis { decrypted = decryptor.decryptString(encrypted, true) } / 1000f

        val outFile = File("out/${inFile.nameWithoutExtension}.txt")
        outFile.createNewFile()
        outFile.writeText(decrypted)

        println("Расшифрованный файл: out/${outFile.name} (${encryptionTime}с.)")

        println("\nДля продолжения нажмите Enter...")
        scanner.nextLine()
    }
}