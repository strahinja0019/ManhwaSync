//AppTheme.kt
package mt.edu.mcast.webapitutorial_ktor.ui.theme

enum class AppTheme(val displayName: String) {
    CRIMSON("Crimson"),
    EMERALD("Emerald"),
    OCEAN("Ocean"),
    MONO("Mono");

    companion object {
        fun fromString(value: String): AppTheme =
            entries.firstOrNull { it.name == value } ?: OCEAN
    }
}