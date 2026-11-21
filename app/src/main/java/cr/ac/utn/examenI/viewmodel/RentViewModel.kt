package cr.ac.utn.examenI.viewmodel

import androidx.lifecycle.ViewModel
import cr.ac.utn.examenI.data.memory.MemoryDataManager
import cr.ac.utn.examenI.domain.model.Rent

class RentViewModel : ViewModel() {
    companion object {
        private val repository = MemoryDataManager()

        fun getAll(): List<Rent> {
            return repository.getAll().filterIsInstance<Rent>()
        }

        fun getById(id: String): Rent? {
            return repository.getById(id) as? Rent
        }

        fun save(item: Rent) {
            repository.add(item)
        }

        fun update(item: Rent) {
            repository.update(item)
        }

        fun delete(id: String) {
            repository.delete(id)
        }
    }
}