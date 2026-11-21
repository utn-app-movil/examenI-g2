package cr.ac.utn.examenI.domain.model

import java.time.LocalDateTime

data class InvInventory(
    override val id: String,
    val person: Person,
    val productName: String,
    val quantity: Int,
    val supplier: String,
    val unitCost: Double,
    val entryDateTime: LocalDateTime
) : Identifier(id) {

    val productCode: String
        get() = id

    override val fullName: String
        get() = "$productName - ${person.fullName}"
}

