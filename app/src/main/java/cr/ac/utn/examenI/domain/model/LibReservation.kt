package cr.ac.utn.examenI.domain.model

import java.time.LocalDateTime

class LibReservation(
    id: String = "",
    name: String = "",
    firstName: String = "",
    lastName: String = "",
    phone: Int = 0,
    email: String = "",
    address: String = "",
    val reservedBooks: List<String> = emptyList(),
    val branch: String = "",
    val reservationDateTime: LocalDateTime = LocalDateTime.now(),
    val returnDateTime: LocalDateTime = LocalDateTime.now().plusDays(7)
) : Person(id = id, name = name, firstName = firstName, lastName = lastName,
    phone = phone, email = email, address = address) {
    override val fullName: String
        get() = "$name $firstName $lastName - Branch: $branch".trim()
}
