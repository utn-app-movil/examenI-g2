package cr.ac.utn.examenI.domain.model

class CliClient(
    override val id: String = "",
    override val name: String = "",
    override val firstName: String = "",
    override val lastName: String = "",
    override val phone: Int = 0,
    override val email: String = "",
    override val address: String = "",
    val companyName: String = "",
    val corporateId: String = "",
    val province: String = "",
    val canton: String = "",
    val district: String = "",
    val services: List<String> = emptyList()
) : Person(id, name, firstName, lastName, phone, email, address) {
    override val fullName: String
        get() = "$companyName ($corporateId)"
}