
package cr.ac.utn.examenI.viewmodel

import cr.ac.utn.examenI.data.memory.MemoryDataManager
import cr.ac.utn.examenI.domain.model.PhaPatient

object PhaViewModel {

    private val repository = MemoryDataManager()

    fun save(item: PhaPatient) {
        repository.add(item)
    }

    fun update(item: PhaPatient) {
        repository.update(item)
    }

    fun delete(id: String) {
        repository.delete(id)
    }

    fun getById(id: String): PhaPatient? {
        return repository.getById(id) as? PhaPatient
    }

    fun getAll(): List<PhaPatient> {
        return repository.getAll().filterIsInstance<PhaPatient>()
    }
}
