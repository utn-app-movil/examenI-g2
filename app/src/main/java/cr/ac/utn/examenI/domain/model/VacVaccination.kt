package cr.ac.utn.examenI.domain.model

import java.time.LocalDateTime

class VacVaccination(
    id: String = "",
    name: String = "",
    firstName: String = "",
    lastName: String = "",
    phone: Int = 0,
    email: String = "",
    address: String = "",
    val vaccineType: VacVaccineType = VacVaccineType.COVID_19,
    val location: VacLocation = VacLocation.PUNTARENAS_HOSPITAL,
    val vaccinationDateTime: LocalDateTime = LocalDateTime.now()
) : Person(
    id = id,
    name = name,
    firstName = firstName,
    lastName = lastName,
    phone = phone,
    email = email,
    address = address
)