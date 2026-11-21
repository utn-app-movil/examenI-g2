package cr.ac.utn.examenI.notif

import cr.ac.utn.examenI.domain.model.Identifier
import cr.ac.utn.examenI.domain.model.Person
import java.time.LocalDateTime

class NotifEmail(
    override val id: String,
    val sender: Person,
    val title: String,
    val message: String,
    val sendDateTime: LocalDateTime,
    val cc: String,
    val cco: String,
    val recipients: List<Person>
) : Identifier(id) {

    // The list adapter shows fullName, so we show the email title there
    override val fullName: String
        get() = title
}