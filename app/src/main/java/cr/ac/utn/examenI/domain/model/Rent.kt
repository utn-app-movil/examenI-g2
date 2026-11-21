package cr.ac.utn.examenI.domain.model

class Rent(
    id: String = "",
    name: String = "",
    firstName: String = "",
    lastName: String = "",
    phone: Int = 0,
    email: String = "",
    address: String = "",
    val vehicleType: String = "",
    val plate: String = "",
    val mileage: Double = 0.0,
    val driverLicense: String = ""
) : Person(id, name, firstName, lastName, phone, email, address)