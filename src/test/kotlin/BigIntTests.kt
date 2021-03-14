import net.catstack.rsa.BigInt
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BigIntTests {
    @Test
    fun creationAndToStringTest() {
        assertEquals("-198257329801567832561879234501", BigInt("-198257329801567832561879234501").toString())
        assertEquals("198257329801567832561879234501", BigInt("198257329801567832561879234501").toString())
        assertEquals("-198257329801567832561879234501", BigInt("-00000198257329801567832561879234501").toString())
        assertEquals("198257329801567832561879234501", BigInt("00000198257329801567832561879234501").toString())
        assertTrue(BigInt("-19385798374").isNegative)
        assertFalse(BigInt("19385798374").isNegative)
    }

    @Test
    fun intCreationTest() {
        assertTrue(BigInt(0) == BigInt("0"))
        assertTrue(BigInt(-0) == BigInt("0"))
        assertTrue(BigInt(-0) == BigInt("-0"))
        assertTrue(BigInt(0) == BigInt("-0"))
        assertTrue(BigInt(398256135) == BigInt("398256135"))
        assertTrue(BigInt(-398256135) == BigInt("-398256135"))
    }

    @Test
    fun negateTest() {
        assertEquals("-198257329801567832561879234501", BigInt("198257329801567832561879234501").negate().toString())
        assertEquals("198257329801567832561879234501", BigInt("-198257329801567832561879234501").negate().toString())
    }

    @Test
    fun plusTest() {
        assertEquals(BigInt.ZERO, BigInt("-100") + BigInt("100"))
        assertEquals(BigInt("200"), BigInt("100") + BigInt("100"))
        assertEquals(BigInt("200"), BigInt("200") + BigInt("0"))
        assertEquals(BigInt("200"), BigInt("0") + BigInt("200"))
        assertEquals(BigInt("200"), BigInt("300") + BigInt("-100"))
        assertEquals(BigInt("200"), BigInt("-100") + BigInt("300"))
        assertEquals(BigInt("1000"), BigInt("999") + BigInt("1"))
        assertEquals(BigInt("999999999999999999999999999999"), BigInt("1000000000000000000000000000000") + BigInt("-1"))

        assertEquals(BigInt("9937818258931249044358390497066571793108478890483817369936097572336050196796077849433650328549592347050379338046725433306104500787896907885217343410962327385662266148204985640694673855190294016594845244230842695773873444490"),
            BigInt("812946089123750981623409817234095812946088329570982679123750981623409817234095812946089123750981623409817234095812946089123750981623409817234095812946088329570982679123750981623409817234095812946089123750981623409817234095")
                    + BigInt("09124872169807498062734980679832475980162390560912834690812346590712640379561982036487561204798610723640562103950912487216980749806273498067983247598016239056091283469081234659071264037956198203648756120479861072364056210395"))
    }

    @Test
    fun minusTest() {
        assertEquals(BigInt.ZERO, BigInt("100") - BigInt("100"))
        assertEquals(BigInt.ZERO, BigInt("-100") - BigInt("-100"))
        assertEquals(BigInt(99), BigInt(100) - BigInt(1))
    }

    @Test
    fun compareToTest() {
        assertTrue(BigInt("57382859327") == BigInt("57382859327"))
        assertTrue(BigInt("-57382859327") == BigInt("-57382859327"))

        assertTrue(BigInt("57382859328") > BigInt("57382859327"))
        assertTrue(BigInt("-57382859328") < BigInt("-57382859327"))

        assertTrue(BigInt("57382859328") > BigInt("-57382859327"))
        assertTrue(BigInt("-57382859328") < BigInt("57382859327"))

        assertTrue(BigInt("57382859328") < BigInt("57382859329"))
        assertTrue(BigInt("-57382859328") > BigInt("-57382859329"))

        assertTrue(BigInt("573828593272") > BigInt("57382859327"))
        assertTrue(BigInt("-573828593272") < BigInt("-57382859327"))

        assertTrue(BigInt("57382859327") < BigInt("573828593272"))
        assertTrue(BigInt("-57382859327") > BigInt("-573828593272"))

        assertFalse(BigInt("57382859327") != BigInt("57382859327"))
        assertFalse(BigInt("-57382859327") != BigInt("-57382859327"))

        assertTrue(BigInt("57382859323") != BigInt("57382859327"))
        assertTrue(BigInt("-57382859323") != BigInt("-57382859327"))
    }

    @Test
    fun hashCodeTest() {
        assertTrue(BigInt("10").hashCode() == BigInt("10").hashCode())
        assertTrue(BigInt("10").hashCode() != BigInt("-10").hashCode())
    }
}