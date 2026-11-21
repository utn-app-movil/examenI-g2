package cr.ac.utn.examenI.domain.model

import java.time.LocalDateTime

class Train(
    id: String,
    name: String,
    firstName: String,
    lastName: String,
    phone: Int,
    email: String,
    address: String,
    val train_courses: List<String>,
    val train_trainingDate: LocalDateTime
) : Person(
    id = id,
    name = name,
    firstName = firstName,
    lastName = lastName,
    phone = phone,
    email = email,
    address = address
)