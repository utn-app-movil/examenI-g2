package cr.ac.utn.examenI.viewmodel

import androidx.lifecycle.ViewModel
import cr.ac.utn.examenI.data.memory.MemoryDataManager
import cr.ac.utn.examenI.domain.model.ship_ContainerMaintenance
import cr.ac.utn.examenI.domain.repository.IRepository
import java.time.LocalDateTime

class ship_MaintenanceViewModel : ViewModel() {

    companion object {
        // Single shared instance so data survives between activities.
        private val repository: IRepository = MemoryDataManager()
        private val ALPHANUMERIC_REGEX = Regex("^[A-Za-z0-9]+$")
    }

    fun getAll(): List<ship_ContainerMaintenance> =
        repository.getAll().filterIsInstance<ship_ContainerMaintenance>()

    fun getById(id: String): ship_ContainerMaintenance? =
        repository.getById(id) as? ship_ContainerMaintenance

    fun exists(containerNumber: String): Boolean = repository.getById(containerNumber) != null

    fun add(item: ship_ContainerMaintenance) = repository.add(item)

    fun update(item: ship_ContainerMaintenance) = repository.update(item)

    fun delete(containerNumber: String) = repository.delete(containerNumber)

    fun isAlphanumeric(value: String): Boolean = ALPHANUMERIC_REGEX.matches(value)

    fun isNotFuture(dateTime: LocalDateTime): Boolean = !dateTime.isAfter(LocalDateTime.now())
}