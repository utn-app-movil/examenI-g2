package cr.ac.utn.examenI.viewmodel

import androidx.lifecycle.ViewModel
import cr.ac.utn.examenI.data.memory.MemoryDataManager
import cr.ac.utn.examenI.domain.model.EveEvent

class EveViewModel : ViewModel() {

    companion object {
        private val repository = MemoryDataManager()

        fun getAll(): List<EveEvent> {
            return repository.getAll().filterIsInstance<EveEvent>()
        }

        fun getById(id: String): EveEvent? {
            return repository.getById(id) as? EveEvent
        }

        fun save(event: EveEvent) {
            repository.add(event)
        }

        fun update(event: EveEvent) {
            repository.update(event)
        }

        fun delete(id: String) {
            repository.delete(id)
        }
    }
}
