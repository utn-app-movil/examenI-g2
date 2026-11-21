
package cr.ac.utn.examenI.domain.model

import java.time.LocalDateTime

class PhaPatient(
    override val id: String = "",
    override val name: String = "",
    override val firstName: String = "",
    override val lastName: String = "",
    override val phone: Int = 0,
    override val email: String = "",
    override val address: String = "",
    val medicines: List<String> = emptyList(),
    val totalAmount: Double = 0.0,
    val purchaseDateTime: LocalDateTime = LocalDateTime.now()
) : Person(
    id = id,
    name = name,
    firstName = firstName,
    lastName = lastName,
    phone = phone,
    email = email,
    address = address
)
