
package cr.ac.utn.examenI.domain.model

import java.time.LocalDateTime

class LicLicenseRenewal(
    override val id: String,
    override val name: String,
    override val firstName: String,
    override val lastName: String,
    override val phone: Int,
    override val email: String,
    override val address: String,
    val licenseType: String,
    val medicalCode: String,
    val score: Int,
    val renewalDateTime: LocalDateTime
) : Person(
    id = id,
    name = name,
    firstName = firstName,
    lastName = lastName,
    phone = phone,
    email = email,
    address = address
)

