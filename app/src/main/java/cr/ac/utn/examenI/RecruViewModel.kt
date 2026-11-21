package cr.ac.utn.examenI.viewmodel

import androidx.lifecycle.ViewModel
import cr.ac.utn.examenI.data.memory.MemoryDataManager
import cr.ac.utn.examenI.domain.model.Identifier

class RecruViewModel : ViewModel() {

    companion object {

        private val manager = MemoryDataManager()

        fun save(obj: Identifier) {
            manager.add(obj)
        }

        fun getAll(): List<Identifier> {
            return manager.getAll()
        }

        fun getById(id: String): Identifier? {
            return manager.getById(id)
        }

        fun update(obj: Identifier) {
            manager.update(obj)
        }

        fun delete(id: String) {
            manager.delete(id)
        }
    }
}

