package cr.ac.utn.examenI.domain.model

enum class VacLocation(val displayName: String) {
    PUNTARENAS_HOSPITAL("Puntarenas Hospital"),
    CHACARITA_EBAIS("Chacarita EBAIS"),
    ESPARZA_CLINIC("Esparza Clinic"),
    BARRANCA_CLINIC("Barranca Clinic");

    override fun toString(): String = displayName
}