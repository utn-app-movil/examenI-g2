package cr.ac.utn.examenI.domain.model

enum class VacVaccineType(val displayName: String) {
    COVID_19("COVID-19"),
    INFLUENZA("Influenza"),
    TETANUS("Tetanus"),
    HEPATITIS_B("Hepatitis B"),
    MEASLES("Measles");

    override fun toString(): String = displayName
}