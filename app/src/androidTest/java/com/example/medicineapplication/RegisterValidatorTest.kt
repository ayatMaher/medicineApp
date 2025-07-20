package com.example.medicineapplication

import com.example.medicineapplication.validator.RegisterValidator
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class RegisterValidatorTest {
    private val validator = RegisterValidator()

    @Test
    fun validName_ReturnsTrue() {
        assertTrue(validator.isValidName("Ayat"))
    }

    @Test
    fun shortName_ReturnsFalse() {
        assertFalse(validator.isValidName("A"))
    }

    @Test
    fun validEmail_ReturnsTrue() {
        assertTrue(validator.isValidEmail("test@example.com"))
    }

    @Test
    fun invalidEmail_ReturnsFalse() {
        assertFalse(validator.isValidEmail("invalid-email"))
    }

    @Test
    fun validPhone_ReturnsTrue() {
        assertTrue(validator.isValidPhone("0599123456"))
    }

    @Test
    fun shortPhone_ReturnsFalse() {
        assertFalse(validator.isValidPhone("059"))
    }

    @Test
    fun validPassword_ReturnsTrue() {
        assertTrue(validator.isValidPassword("123456"))
    }

    @Test
    fun shortPassword_ReturnsFalse() {
        assertFalse(validator.isValidPassword("123"))
    }

    @Test
    fun allValidInputs_ReturnsTrue() {
        assertTrue(validator.validateAll("Ayat", "test@example.com", "0599123456", "123456"))
    }

    @Test
    fun invalidInputs_ReturnsFalse() {
        assertFalse(validator.validateAll("", "invalid", "059", "123"))
    }
}