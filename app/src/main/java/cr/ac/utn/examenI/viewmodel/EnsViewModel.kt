package cr.ac.utn.examenI.viewmodel

import androidx.lifecycle.ViewModel
import cr.ac.utn.examenI.data.memory.MemoryDataManager
import cr.ac.utn.examenI.domain.model.EnsPolicy
import cr.ac.utn.examenI.domain.repository.IRepository

class EnsViewModel : ViewModel() {
    companion object {
        private val repository: IRepository = MemoryDataManager()

        fun getAll(): List<EnsPolicy> = repository.getAll().filterIsInstance<EnsPolicy>()

        fun getById(id: String): EnsPolicy? = repository.getById(id) as? EnsPolicy

        fun existsPolicyNum(policyNum: String, excludeId: String): Boolean {
            return getAll().any {
                it.policyNum.equals(policyNum, ignoreCase = true) && it.id.trim() != excludeId.trim()
            }
        }

        fun save(item: EnsPolicy) {
            repository.add(item)
        }

        fun update(item: EnsPolicy) {
            repository.update(item)
        }

        fun delete(id: String) {
            repository.delete(id)
        }
    }
}
