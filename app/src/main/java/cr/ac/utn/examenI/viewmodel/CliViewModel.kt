package cr.ac.utn.examenI.viewmodel

import cr.ac.utn.examenI.data.memory.MemoryDataManager
import cr.ac.utn.examenI.domain.model.CliClient
import cr.ac.utn.examenI.domain.model.Identifier


class CliViewModel {

    companion object{
        private val manager = MemoryDataManager()

        fun getAll(): List<Identifier> = manager.getAll()

        fun getById(id: String): CliClient? = manager.getById(id) as? CliClient

        fun add(client: CliClient) = manager.add(client)

        fun update(client: CliClient) = manager.update(client)

        fun delete(id: String) = manager.delete(id)

        fun existsByCorporateId(corporateId: String, excludeId: String = ""): Boolean {
            return manager.getAll()
                .filterIsInstance<CliClient>()
                .any { it.corporateId == corporateId && it.id != excludeId }
        }
    }

}