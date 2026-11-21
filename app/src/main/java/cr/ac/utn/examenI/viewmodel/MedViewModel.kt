package cr.ac.utn.examenI.viewmodel

import cr.ac.utn.examenI.data.memory.MemoryDataManager
import cr.ac.utn.examenI.domain.model.MedCheckup
import cr.ac.utn.examenI.domain.repository.IRepository

object MedViewModel {
    private val repository: IRepository = MemoryDataManager()

    fun getAll(): List<MedCheckup> =
        repository.getAll().filterIsInstance<MedCheckup>().sortedByDescending { it.checkDateTime }

    fun getById(id: String): MedCheckup? = repository.getById(id) as? MedCheckup

    fun save(item: MedCheckup) = repository.add(item)

    fun update(item: MedCheckup) = repository.update(item)

    fun delete(id: String) = repository.delete(id)
}