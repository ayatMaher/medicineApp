package com.example.medicineapplication

import com.example.medicineapplication.validator.DrugNameExtractor
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class DrugNameExtractorTest {
    private val extractor = DrugNameExtractor()

    @Test
    fun extract_OneDrug_ReturnsCorrectArabic() {
        val result = extractor.extract("Take Panadol daily")
        assertEquals(listOf("بنادول"), result)
    }

    @Test
    fun extract_MultipleDrugs_ReturnsAllArabic() {
        val result = extractor.extract("Use Brufen and Amoxicillin")
        assertTrue(result.contains("بروفين"))
        assertTrue(result.contains("أموكسيسيلين"))
    }

    @Test
    fun extract_NoDrugInText_ReturnsEmptyList() {
        val result = extractor.extract("Just rest and drink water")
        assertTrue(result.isEmpty())
    }
}