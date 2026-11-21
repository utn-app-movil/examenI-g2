package cr.ac.utn.examenI.viewmodel

import androidx.lifecycle.ViewModel
import cr.ac.utn.examenI.data.memory.MemoryDataManager
import cr.ac.utn.examenI.domain.model.PayenRequest
import cr.ac.utn.examenI.domain.repository.IRepository

class PayenViewModel : ViewModel() {
    companion object {
        private val repository: IRepository = MemoryDataManager()

        fun getAll(): List<PayenRequest> {
            return repository.getAll().filterIsInstance<PayenRequest>()
        }

        fun getById(id: String): PayenRequest? {
            return repository.getById(id) as? PayenRequest
        }

        fun create(request: PayenRequest): Boolean {
            if (getById(request.id) != null)
                return false
            repository.add(request)
            return true
        }

        fun update(request: PayenRequest): Boolean {
            if (getById(request.id) == null)
                return false
            repository.update(request)
            return true
        }

        fun delete(id: String): Boolean {
            if (getById(id) == null)
                return false
            repository.delete(id)
            return true
        }
    }
}
