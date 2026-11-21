package cr.ac.utn.examenI.domain.model

import java.time.LocalDate

class mark_Campaign(
    val code: String,
    val creatorId: String,
    name: String,
    firstName: String,
    lastName: String,
    phone: Int,
    email: String,
    address: String,
    val campaignName: String,
    val budget: Double,
    val channel: mark_Channel,
    val leader: String,
    val province: mark_Province,
    val startDate: LocalDate,
    val endDate: LocalDate
) : Person(
    id = code,
    name = name,
    firstName = firstName,
    lastName = lastName,
    phone = phone,
    email = email,
    address = address
) {
    override val fullName: String
        get() = "$campaignName - ${channel.displayName}"
}