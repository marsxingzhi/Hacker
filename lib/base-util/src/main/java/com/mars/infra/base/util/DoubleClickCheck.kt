package com.mars.infra.base.util

import android.view.View
import java.util.WeakHashMap


/**
 * Created by Mars on 2022/4/21
 */
object DoubleClickCheck {

    private const val DEFAULT_SPACE_TIME = 500L
    private val LAST_CLICK_MAP = WeakHashMap<View, Long>()

    fun clear() {
        LAST_CLICK_MAP.clear()
    }

    fun isDoubleClick(view: View?): Boolean {
        return isDoubleClick(view, DEFAULT_SPACE_TIME)
    }

    fun isDoubleClick(view: View?, spaceTime: Long): Boolean {
        var doubleCheck = false
        if (view == null) {
            return false
        }
        val lastClickTime = LAST_CLICK_MAP[view]
        val currentTime = System.currentTimeMillis()
        if (lastClickTime == null) {
            LAST_CLICK_MAP[view] = currentTime
            return false
        }
        if (currentTime - lastClickTime <= spaceTime) {
            doubleCheck = true
        }
        if (!doubleCheck) {
            LAST_CLICK_MAP[view] = currentTime
        }
        return doubleCheck
    }
}