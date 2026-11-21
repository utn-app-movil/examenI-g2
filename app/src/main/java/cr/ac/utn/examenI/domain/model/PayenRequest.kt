package cr.ac.utn.examenI.domain.model

import java.time.LocalDateTime

class PayenRequest(
    override val id: String,
    val person: Person,
    val specialty: String,
    val appointmentDateTime: LocalDateTime,
    val requestDateTime: LocalDateTime,
    val appointmentCost: Double,
    val medicineCost: Double,
    val medicines: List<String>
) : Identifier(id) {
    override val fullName: String
        get() = person.fullName

    val reimbursementAmount: Double
        get() = (appointmentCost + medicineCost) * 0.80
}
