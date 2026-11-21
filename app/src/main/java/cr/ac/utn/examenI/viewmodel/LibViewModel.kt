package cr.ac.utn.examenI.viewmodel

import androidx.lifecycle.ViewModel
import cr.ac.utn.examenI.data.memory.MemoryDataManager
import cr.ac.utn.examenI.domain.model.LibReservation

class LibViewModel : ViewModel() {
    companion object {
        private val repository = MemoryDataManager()

        fun getAll(): List<LibReservation> {
            return repository.getAll().filterIsInstance<LibReservation>()
        }

        fun getById(id: String): LibReservation? {
            return repository.getById(id) as? LibReservation
        }

        fun save(item: LibReservation) {
            repository.add(item)
        }

        fun update(item: LibReservation) {
            repository.update(item)
        }

        fun delete(id: String) {
            repository.delete(id)
        }
    }
}
