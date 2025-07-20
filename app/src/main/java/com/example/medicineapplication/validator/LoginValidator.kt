package com.example.medicineapplication.validator

class LoginValidator {
    fun isValidEmail(email: String): Boolean {
        // التحقق من أن الايميل غير فارغ ويتبع نمط ايميل صحيح
        return email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        // كلمة المرور على الأقل 6 أحرف
        return password.length >= 6
    }
}