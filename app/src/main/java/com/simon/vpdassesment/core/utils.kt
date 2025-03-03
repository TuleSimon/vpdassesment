package com.simon.vpdassesment.core

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun View.addSafeArea() = ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
    val info = view.getInfo()
    if (!info.getBoolean("safeAreaAdded", false)) {
        val insets =
            windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
        view.setPadding(
            view.paddingLeft + insets.left,
            view.paddingTop + insets.top,
            view.paddingRight + insets.right,
            view.paddingBottom + insets.bottom
        )
        info.putBoolean("safeAreaAdded", true)
    }
    WindowInsetsCompat.CONSUMED
}

fun View.getInfo(): Bundle {
    if (tag as? Bundle == null) {
        tag = Bundle()
    }
    return tag as Bundle
}
