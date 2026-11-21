package cr.ac.utn.examenI.domain.model

import java.time.LocalDateTime

class FliFlight (
    id: String= "",
    name: String= "",
    firstName: String="",
    lastName: String="",
    phone: Int= 0,
    email: String="",
    address: String="",
    val originCountry: Country = Country.COSTA_RICA,
    val destCountry: Country = Country.MEXICO,
    val flightNumber: String = "",
    val flightDateTime: LocalDateTime = LocalDateTime.now()
) : Person (
    id = id,
    name = name,
    firstName = firstName,
    lastName = lastName,
    phone = phone,
    email = email,
    address = address,

)