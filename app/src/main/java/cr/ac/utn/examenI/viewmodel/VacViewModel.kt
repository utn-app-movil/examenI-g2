package cr.ac.utn.examenI.viewmodel

import androidx.lifecycle.ViewModel
import cr.ac.utn.examenI.data.memory.MemoryDataManager
import cr.ac.utn.examenI.domain.model.VacVaccination
import java.time.LocalDateTime

class VacViewModel : ViewModel() {

    companion object {
        // Shared between the list and CRUD screens so the data survives navigation
        private val repository = MemoryDataManager()
    }

    fun getAll(): List<VacVaccination> =
        repository.getAll().filterIsInstance<VacVaccination>()

    fun getById(id: String): VacVaccination? =
        repository.getById(id) as? VacVaccination

    fun exists(id: String): Boolean = getById(id) != null

    /** Returns false when a record with the same id already exists. */
    fun save(item: VacVaccination): Boolean {
        if (exists(item.id)) return false
        repository.add(item)
        return true
    }

    fun update(item: VacVaccination) {
        repository.update(item)
    }

    fun delete(id: String) {
        repository.delete(id)
    }

    /** The vaccination must already have happened: not after the current moment. */
    fun isDateTimeValid(dateTime: LocalDateTime): Boolean =
        !dateTime.isAfter(LocalDateTime.now())
}