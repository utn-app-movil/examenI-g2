
package cr.ac.utn.examenI.viewmodel

import androidx.lifecycle.ViewModel
import cr.ac.utn.examenI.data.memory.MemoryDataManager
import cr.ac.utn.examenI.domain.model.LicLicenseRenewal

class LicViewModel : ViewModel() {

    companion object {
        private val repository = MemoryDataManager()
    }

    fun getAll(): List<LicLicenseRenewal> {
        return repository.getAll().filterIsInstance<LicLicenseRenewal>()
    }


    fun getById(id: String): LicLicenseRenewal? {
        return repository.getById(id) as? LicLicenseRenewal
    }


    fun save(item: LicLicenseRenewal): Boolean {
        if (getById(item.id) != null) {
            return false
        }

        repository.add(item)
        return true
    }


    fun update(item: LicLicenseRenewal) {
        repository.update(item)
    }


    fun delete(id: String) {
        repository.delete(id)
    }






}
