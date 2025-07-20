package com.example.medicineapplication

import com.example.medicineapplication.validator.LoginValidator
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class LoginValidatorTest {
    private val validator = LoginValidator()
    @Test
    fun emailValidation_CorrectEmail_ReturnsTrue() {
        assertTrue(validator.isValidEmail("test@example.com"))
    }

    @Test
    fun emailValidation_EmptyEmail_ReturnsFalse() {
        assertFalse(validator.isValidEmail(""))
    }
    @Test
    fun emailValidation_InvalidEmail_ReturnsFalse() {
        assertFalse(validator.isValidEmail("invalid-email"))
    }

    @Test
    fun passwordValidation_ValidPassword_ReturnsTrue() {
        assertTrue(validator.isValidPassword("123456"))
    }

    @Test
    fun passwordValidation_ShortPassword_ReturnsFalse() {
        assertFalse(validator.isValidPassword("123"))
    }

}