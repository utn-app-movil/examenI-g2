package cr.ac.utn.examenI.domain.model

data class Bid(
    override val id: String = "",
    override val name: String = "",
    override val firstName: String = "",
    override val lastName: String = "",
    override val phone: Int = 0,
    override val email: String = "",
    override val address: String = "",
    val articleCode: String = "",
    val articleDescription: String = "",
    val amount: Double = 0.0,
    val dateTime: String = "",
    val awarded: Boolean = false
) : Person(id, name, firstName, lastName, phone, email, address) {
    override val fullName: String
        get() = "$name $firstName $lastName".trim()
}
