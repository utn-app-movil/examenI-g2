package cr.ac.utn.examenI.domain.model

import java.time.LocalDateTime

class ship_ContainerMaintenance(
    val containerNumber: String,
    val person: Person,
    val maintenanceDateTime: LocalDateTime,
    val containerType: ship_ContainerType,
    val temperature: Double,
    val weight: Double,
    val product: ship_Product
) : Identifier(containerNumber) {
    override val fullName: String
        get() = person.fullName
}