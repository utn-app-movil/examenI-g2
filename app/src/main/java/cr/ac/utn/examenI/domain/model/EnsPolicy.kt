package cr.ac.utn.examenI.domain.model

import java.time.LocalDate

class EnsPolicy(
    id: String,
    name: String,
    firstName: String,
    lastName: String,
    phone: Int,
    email: String,
    address: String,
    val policyNum: String,
    val company: EnsCompany,
    val insuranceType: EnsInsuranceType,
    val premium: Double,
    val startDate: LocalDate,
    val expirationDate: LocalDate
) : Person(id, name, firstName, lastName, phone, email, address)
