package com.dimmaranch.skull

actual object PlatformUtils {
    actual fun isAndroid(): Boolean {
        return true
    }

    actual fun isIOS(): Boolean {
        return false
    }
}