package com.dimmaranch.skull

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform