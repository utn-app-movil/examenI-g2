package cr.ac.utn.examenI.domain.model

enum class EnsInsuranceType(
    val displayName: String
) {
    CIVIL_LIABILITY("Civil Liability"),
    FIRE("Fire"),
    OCCUPATIONAL_RISK("Occupational Risk"),
    FLEET("Fleet");

    override fun toString(): String = displayName
}
