package com.example.medicineapplication.validator

import android.util.Patterns

class RegisterValidator {
    fun isValidName(name: String): Boolean {
        return name.trim().length >= 3
    }

    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    }

    fun isValidPhone(phone: String): Boolean {
        return phone.trim().length >= 6 && phone.all { it.isDigit() }
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun validateAll(name: String, email: String, phone: String, password: String): Boolean {
        return isValidName(name) &&
                isValidEmail(email) &&
                isValidPhone(phone) &&
                isValidPassword(password)
    }
}