package cr.ac.utn.examenI.domain.model

import java.time.LocalDateTime

class Recruitment(
    override val id: String,
    override val name: String,
    override val firstName: String,
    override val lastName: String,
    override val phone: Int,
    override val email: String,
    override val address: String,
    val province: String,
    val canton: String,
    val district: String,
    val targetCompany: String,
    val roles: List<String>,
    val salaryExpectation: Double,
    val experienceYears: Int,
    val registrationDateTime: LocalDateTime
) : Person(
    id = id,
    name = name,
    firstName = firstName,
    lastName = lastName,
    phone = phone,
    email = email,
    address = address
)
