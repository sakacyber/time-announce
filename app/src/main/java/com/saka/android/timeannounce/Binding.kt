package com.saka.android.timeannounce

import android.view.View
import androidx.databinding.BindingAdapter

object Binding {

    @JvmStatic
    @BindingAdapter("shouldShow")
    fun shouldShow(view: View, isShow: Boolean = false) {
        if (isShow) view.visibility = View.VISIBLE else view.visibility = View.GONE
    }
}
