package cr.ac.utn.examenI.viewmodel

import cr.ac.utn.examenI.data.memory.MemoryDataManager
import cr.ac.utn.examenI.domain.model.mark_Campaign
import cr.ac.utn.examenI.domain.repository.IRepository

object mark_ViewModel {
    private val repository: IRepository = MemoryDataManager()

    fun getAll(): List<mark_Campaign> {
        return repository.getAll().filterIsInstance<mark_Campaign>()
    }

    fun getById(id: String): mark_Campaign? {
        return repository.getById(id) as? mark_Campaign
    }

    fun exists(code: String): Boolean {
        return getAll().any { it.code.equals(code.trim(), ignoreCase = true) }
    }

    fun save(item: mark_Campaign) {
        repository.add(item)
    }

    fun update(item: mark_Campaign) {
        repository.update(item)
    }

    fun delete(id: String) {
        repository.delete(id)
    }
}