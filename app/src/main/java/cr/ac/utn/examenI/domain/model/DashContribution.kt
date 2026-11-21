package cr.ac.utn.examenI.domain.model

class DashContribution (
    val personId: String = "",
            override val name: String = "",
            override val firstName: String = "",
            override val lastName: String = "",
            override val phone: Int = 0,
            override val email: String = "",
            override val address: String = "",
            val contributionsTotal: Double = 0.0,
            val day: Int = 1,
            val month: Int = 1,
            val year: Int = 2025,
    ) : Person(
        id = "$personId-$month-$year",
        name = name,
        firstName = firstName,
        lastName = lastName,
        phone = phone,
        email = email,
        address = address,

    ){
        override val fullName: String
            get() = "$name $firstName $lastName $contributionsTotal ($day/$month/$year)"
    }


