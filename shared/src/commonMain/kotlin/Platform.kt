enum class PlatformType { ANDROID, IOS }

expect object Platform {
    val type: PlatformType
    val androidSdkVersion: Int?
    val iosVersion: String?
}