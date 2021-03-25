import net.catstack.rsa.crypt.Decryptor
import net.catstack.rsa.crypt.Encryptor
import net.catstack.rsa.crypt.KeysGenerator
import net.catstack.rsa.utils.BigInt
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

class RSATests {
    private val keyGenerator1 = KeysGenerator(BigInt(13), BigInt(17))
    private val keyGenerator2 = KeysGenerator(BigInt(19), BigInt(17))

    private val encryptor1 = Encryptor(keyGenerator1.generatePublicKey())
    private val encryptor2 = Encryptor(keyGenerator2.generatePublicKey())

    private val decryptor1 = Decryptor(keyGenerator1.generatePrivateKey())
    private val decryptor2 = Decryptor(keyGenerator2.generatePrivateKey())

    @Test
    fun keysGenerationDifferenceTest() {
        val publicKey1 = keyGenerator1.generatePublicKey()
        val privateKey1 = keyGenerator1.generatePrivateKey()
        val publicKey2 = keyGenerator2.generatePublicKey()
        val privateKey2 = keyGenerator2.generatePrivateKey()

        assertTrue(publicKey1.mod != publicKey2.mod)
        assertTrue(privateKey1.mod == publicKey1.mod)
        assertTrue(privateKey1.d != publicKey1.e)
        assertTrue(privateKey2.d != publicKey2.e)
    }

    @Test
    fun numberEncryptionTest() {
        val a = BigInt(25)
        val b = BigInt(114)

        val encryptedA1 = encryptor1.encrypt(a)
        val encryptedA2 = encryptor2.encrypt(a)
        val encryptedB1 = encryptor1.encrypt(b)

        assertTrue(a != encryptedA1)
        assertTrue(a != encryptedA2)
        assertTrue(encryptedA1 != encryptedA2)
        assertTrue(encryptedB1 != encryptedA1)
        assertTrue(encryptedB1 != b)
    }

    @Test
    fun numberDecryptionTest() {
        val a = BigInt(25)
        val b = BigInt(114)

        val encryptedA1 = encryptor1.encrypt(a)
        val encryptedA2 = encryptor2.encrypt(a)
        val encryptedB1 = encryptor1.encrypt(b)

        val decryptedA1 = decryptor1.decrypt(encryptedA1)
        val decryptedA2 = decryptor2.decrypt(encryptedA2)
        val decryptedB1 = decryptor1.decrypt(encryptedB1)

        assertEquals(a, decryptedA1)
        assertEquals(a, decryptedA2)
        assertEquals(b, decryptedB1)
    }

    @Test
    fun stringEncryptionAndDecryptionTest() {
        val a = "Привет test\n String"

        val encryptedA1 = encryptor1.encryptString(a)
        val encryptedA2 = encryptor2.encryptString(a)

        assertNotEquals(encryptedA1, encryptedA2)

        val decryptedA1 = decryptor1.decryptString(encryptedA1)
        val decryptedA2 = decryptor2.decryptString(encryptedA2)

        assertEquals(a, decryptedA1)
        assertEquals(a, decryptedA2)
    }
}