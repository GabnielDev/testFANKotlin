package com.example.testfankotlin

import android.text.TextUtils
import java.util.regex.Pattern

fun String.isEmailValid(): Boolean {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun isMinimalPasswordLength(password: String): Boolean {
    val passwordRegex = Pattern.compile(
        "^" +
                ".{8,}" +
                "$"
    )

    return passwordRegex.matcher(password).matches()
}

fun isPasswordContainUppercase(password: String): Boolean {
    val passwordRegex = Pattern.compile(
        "^" +
                "(?=.*[A-Z])" +
                ".{1,}" +
                "$"
    )

    return passwordRegex.matcher(password).matches()
}

fun isPasswordContainLowercase(password: String): Boolean {
    val passwordRegex = Pattern.compile(
        "^" +
                "(?=.*[a-z])" +
                ".{1,}" +
                "$"
    )

    return passwordRegex.matcher(password).matches()
}

fun isPasswordContainNumber(password: String): Boolean {
    val passwordRegex = Pattern.compile(
        "^" +
                "(?=.*[0-9])" +
                ".{1,}" +
                "$"
    )

    return passwordRegex.matcher(password).matches()
}


