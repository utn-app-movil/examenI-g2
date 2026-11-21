package cr.ac.utn.examenI.viewmodel

import cr.ac.utn.examenI.data.memory.MemoryDataManager
import cr.ac.utn.examenI.domain.model.PayPerson
import cr.ac.utn.examenI.domain.repository.IRepository

object PayViewModel {
    private val repository: IRepository = MemoryDataManager()

    fun getAll(): List<PayPerson> = repository.getAll().filterIsInstance<PayPerson>()
    fun getById(id: String): PayPerson? = repository.getById(id) as? PayPerson
    fun save(item: PayPerson) = repository.add(item)
    fun update(item: PayPerson) = repository.update(item)
    fun delete(id: String) = repository.delete(id)
}