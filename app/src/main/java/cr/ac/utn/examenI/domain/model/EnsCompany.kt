package cr.ac.utn.examenI.domain.model

enum class EnsCompany(
    val displayName: String
) {
    INS("INS"),
    ASSA("ASSA"),
    MAPFRE("MAPFRE"),
    BMI("BMI");

    override fun toString(): String = displayName
}
