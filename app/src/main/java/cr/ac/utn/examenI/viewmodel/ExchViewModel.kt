package cr.ac.utn.examenI.viewmodel

import androidx.lifecycle.ViewModel
import cr.ac.utn.examenI.data.memory.MemoryDataManager
import cr.ac.utn.examenI.domain.model.ExchTransaction
import cr.ac.utn.examenI.domain.repository.IRepository

class ExchViewModel : ViewModel() {

    companion object {
        private val repository: IRepository = MemoryDataManager()

        fun getAll(): List<ExchTransaction> {
            val result = mutableListOf<ExchTransaction>()
            for (item in repository.getAll()) {
                if (item is ExchTransaction) {
                    result.add(item)
                }
            }
            return result.sortedByDescending { it.exchangeDateTime }
        }

        fun getById(id: String): ExchTransaction? {
            return repository.getById(id.trim()) as? ExchTransaction
        }

        fun save(item: ExchTransaction) {
            if (repository.getById(item.id) != null)
                throw Exception("The exchange record is duplicated.")
            repository.add(item)
        }

        fun update(item: ExchTransaction) {
            if (repository.getById(item.id) == null)
                throw Exception("It is not possible to update a record that does not exist.")
            repository.update(item)
        }

        fun delete(id: String) {
            val key = id.trim()
            if (repository.getById(key) == null)
                throw Exception("It is not possible to delete a record that does not exist.")
            repository.delete(key)
        }
    }
}