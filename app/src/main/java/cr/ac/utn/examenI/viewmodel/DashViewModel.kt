package cr.ac.utn.examenI.viewmodel

import androidx.lifecycle.ViewModel
import cr.ac.utn.examenI.data.memory.MemoryDataManager
import cr.ac.utn.examenI.domain.model.DashContribution
import cr.ac.utn.examenI.domain.model.Identifier


class DashViewModel : ViewModel() {
    companion object {
        private val dataManager = MemoryDataManager()

        fun getAll(): List<DashContribution> {
            return dataManager.getAll().filterIsInstance<DashContribution>()
        }

        fun getById(id: String): DashContribution? {
            return dataManager.getById(id) as? DashContribution
        }

        fun save(contribution: DashContribution) {
            dataManager.add(contribution)
        }

        fun update(contribution: DashContribution) {
            dataManager.update(contribution)
        }

        fun delete(id: String) {
            dataManager.delete(id)
        }
    }
}