package com.example.scribe

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

fun setFabAttributes(
    applicationContext: Context,
    @DrawableRes imageResId: Int,
    @ColorRes colorResId: Int
) {
    val fab = (applicationContext as Activity).findViewById<FloatingActionButton>(R.id.fab)
    fab.setImageResource(imageResId)
    fab.backgroundTintList = ColorStateList.valueOf(
        ContextCompat.getColor(
            fab.context,
            colorResId))
}