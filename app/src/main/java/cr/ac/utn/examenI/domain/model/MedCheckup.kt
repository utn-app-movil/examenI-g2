package cr.ac.utn.examenI.domain.model

import java.time.LocalDateTime

class MedCheckup(
    id: String,
    name: String,
    firstName: String,
    lastName: String,
    phone: Int,
    email: String,
    address: String,
    val bloodPressure: String,
    val weight: Double,
    val height: Double,
    val oxygenation: Double,
    val checkDateTime: LocalDateTime
) : Person(
    id = id,
    name = name,
    firstName = firstName,
    lastName = lastName,
    phone = phone,
    email = email,
    address = address
)