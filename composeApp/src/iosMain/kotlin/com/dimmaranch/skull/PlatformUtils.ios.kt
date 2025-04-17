package com.dimmaranch.skull

actual object PlatformUtils {
    actual fun isAndroid(): Boolean {
        return false
    }

    actual fun isIOS(): Boolean {
        return true
    }
}