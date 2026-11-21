package cr.ac.utn.examenI.viewmodel

import androidx.lifecycle.ViewModel
import cr.ac.utn.examenI.data.memory.MemoryDataManager
import cr.ac.utn.examenI.domain.model.InvInventory
import cr.ac.utn.examenI.domain.repository.IRepository

class InvViewModel : ViewModel() {

    companion object {

        private val repository: IRepository = MemoryDataManager()

        fun getAll(): List<InvInventory> {
            return repository.getAll()
                .filterIsInstance<InvInventory>()
        }

        fun getById(id: String): InvInventory? {
            return repository.getById(id) as? InvInventory
        }

        fun save(item: InvInventory): Boolean {
            if (repository.getById(item.id) != null) {
                return false
            }

            repository.add(item)
            return true
        }

        fun update(item: InvInventory): Boolean {
            if (getById(item.id) == null) {
                return false
            }

            repository.update(item)
            return true
        }

        fun delete(id: String): Boolean {
            if (getById(id) == null) {
                return false
            }

            repository.delete(id)
            return true
        }
    }
}

