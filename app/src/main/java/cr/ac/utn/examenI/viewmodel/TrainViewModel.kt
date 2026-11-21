
package cr.ac.utn.examenI.viewmodel

import cr.ac.utn.examenI.data.memory.MemoryDataManager
import cr.ac.utn.examenI.domain.model.Train

object TrainViewModel {

    private val train_manager = MemoryDataManager()

    fun train_save(item: Train) {
        train_manager.add(item)
    }

    fun train_update(item: Train) {
        train_manager.update(item)
    }

    fun train_delete(id: String) {
        train_manager.delete(id)
    }

    fun train_getAll(): List<Train> {
        return train_manager.getAll().filterIsInstance<Train>()
    }

    fun train_getById(id: String): Train? {
        return train_manager.getById(id) as? Train
    }
}
