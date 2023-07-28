package utils

object PkceGenerator {
    private const val CODE_VERIFIER_STRING =
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-._~"

    fun generateVerifier(length: Int): String {
        return StringExtensions.getRandomString(length, CODE_VERIFIER_STRING)
    }
}