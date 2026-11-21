package cr.ac.utn.examenI.viewmodel

import cr.ac.utn.examenI.data.memory.MemoryDataManager
import cr.ac.utn.examenI.domain.model.FliFlight
import cr.ac.utn.examenI.domain.repository.IRepository


object FliViewModel {
    private val repository: IRepository = MemoryDataManager()

    fun getAll(): List<FliFlight> {
        return repository.getAll().filterIsInstance<FliFlight>()
    }

    fun getById(id: String): FliFlight? {
        return repository.getById(id) as? FliFlight
    }

    fun save(item: FliFlight) {
        repository.add(item)
    }

    fun update(item: FliFlight) {
        repository.update(item)
    }

    fun delete(id: String) {
        repository.delete(id)
    }
}