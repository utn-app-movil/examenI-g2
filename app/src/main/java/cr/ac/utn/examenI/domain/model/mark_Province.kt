package cr.ac.utn.examenI.domain.model

enum class mark_Province(val displayName: String) {
    SAN_JOSE("San José"),
    ALAJUELA("Alajuela"),
    CARTAGO("Cartago"),
    HEREDIA("Heredia"),
    GUANACASTE("Guanacaste"),
    PUNTARENAS("Puntarenas"),
    LIMON("Limón");

    override fun toString(): String = displayName
}