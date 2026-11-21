package cr.ac.utn.examenI.domain.model

import java.time.LocalDate

class PayPerson(
    override val id: String = "",
    override val name: String = "",
    override val firstName: String = "",
    override val lastName: String = "",
    override val phone: Int = 0,
    override val email: String = "",
    override val address: String = "",
    override val country: Country = Country.COSTA_RICA,
    override val birthDate: LocalDate = LocalDate.now(),
    val employeeNum: String = "",
    val position: String = "",
    val salary: Double = 0.0,
    val iban: String = "",
    val month: Int = 1,
    val bank: String = ""
) : Person(id, name, firstName, lastName, phone, email, address, country, birthDate)