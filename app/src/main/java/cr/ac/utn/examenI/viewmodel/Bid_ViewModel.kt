package cr.ac.utn.examenI.viewmodel

import androidx.lifecycle.ViewModel
import cr.ac.utn.examenI.data.memory.MemoryDataManager
import cr.ac.utn.examenI.domain.model.Bid
import cr.ac.utn.examenI.domain.repository.IRepository

class Bid_ViewModel : ViewModel() {

    companion object {
        private val repository: IRepository = MemoryDataManager()
    }

    fun getAll(): List<Bid> {
        return repository.getAll().map { it as Bid }
    }

    fun getById(id: String): Bid? {
        return repository.getById(id) as? Bid
    }

    fun save(bid: Bid) {
        repository.add(bid)
    }

    fun update(bid: Bid) {
        repository.update(bid)
    }

    fun delete(id: String) {
        repository.delete(id)
    }

    fun existsById(id: String): Boolean {
        return repository.getById(id) != null
    }

    fun getMaxBidAmountForArticle(articleCode: String, excludeId: String = ""): Double {
        return getAll()
            .filter { it.articleCode == articleCode && it.id != excludeId }
            .maxOfOrNull { it.amount } ?: 0.0
    }
}
