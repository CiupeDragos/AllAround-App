package com.example.allaroundapp.other

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.customSnackbar(message: String) {
    Snackbar.make(
        requireView(),
        message,
        Snackbar.LENGTH_LONG
    ).show()
}

fun Fragment.customSnackbar(@StringRes res: Int) {
    Snackbar.make(
        requireView(),
        res,
        Snackbar.LENGTH_LONG
    ).show()
}