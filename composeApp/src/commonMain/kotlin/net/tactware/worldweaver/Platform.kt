package net.tactware.worldweaver

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform