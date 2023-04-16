package ru.netology.nework.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlin.math.ceil

object AndroidUtils {

    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun dp(context: Context, dp: Int): Int =
        ceil(context.resources.displayMetrics.density * dp).toInt()

    fun screenWidth(context: Context) : Int = context.resources.displayMetrics.widthPixels

}