package cr.ac.utn.examenI.domain.model

import java.time.LocalDateTime

class EveEvent(
    override val id: String = "",
    override val name: String = "",
    override val firstName: String = "",
    override val lastName: String = "",
    override val phone: Int = 0,
    override val email: String = "",
    override val address: String = "",
    val institution: String = "",
    val venue: String = "",
    val seatNumber: String = "",
    val eventType: String = "",
    val eventDateTime: LocalDateTime = LocalDateTime.now()
) : Person(
    id = id,
    name = name,
    firstName = firstName,
    lastName = lastName,
    phone = phone,
    email = email,
    address = address
)