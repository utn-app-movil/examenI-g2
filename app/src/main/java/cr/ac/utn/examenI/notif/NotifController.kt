package cr.ac.utn.examenI.notif

import cr.ac.utn.examenI.data.memory.MemoryDataManager
import cr.ac.utn.examenI.domain.model.Person
import cr.ac.utn.examenI.domain.repository.IRepository

object NotifController {
    // Stores the emails
    private val repository: IRepository = MemoryDataManager()

    private val peopleRepository: IRepository = MemoryDataManager()

    private var lastId = 0

    fun nextId(): String {
        lastId++
        return lastId.toString()
    }

    fun getAll(): List<NotifEmail> =
        repository.getAll().filterIsInstance<NotifEmail>()

    fun getById(id: String): NotifEmail? =
        repository.getById(id) as? NotifEmail

    fun isDuplicate(email: NotifEmail, excludeId: String? = null): Boolean =
        getAll().any {
            it.id != excludeId &&
                    it.sender.id == email.sender.id &&
                    it.title.equals(email.title, ignoreCase = true) &&
                    it.sendDateTime == email.sendDateTime
        }

    fun add(email: NotifEmail) = repository.add(email)

    fun update(email: NotifEmail) = repository.update(email)

    fun delete(id: String) = repository.delete(id)

    fun getPeople(): List<Person> =
        peopleRepository.getAll().filterIsInstance<Person>()

    fun getPersonById(id: String): Person? =
        peopleRepository.getById(id) as? Person

    fun savePerson(person: Person) {
        if (getPersonById(person.id) == null) {
            peopleRepository.add(person)
        } else {
            peopleRepository.update(person)
        }
    }
}