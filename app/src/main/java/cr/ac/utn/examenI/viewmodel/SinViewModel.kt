package cr.ac.utn.examenI.viewmodel

import java.time.LocalDateTime
import cr.ac.utn.examenI.domain.model.Identifier

data class Sin(
    override val id: String,
    val name: String,
    val firstName: String,
    val lastName: String,
    val phone: Int,
    val email: String,
    val address: String,
    val originPhone: String,
    val recipientPhone: String,
    val recipientName: String,
    val amount: Double,
    val description: String,
    val sinpeDateTime: LocalDateTime
) : Identifier(id) {
    override val fullName: String
        get() = "$name - $recipientName (₡$amount)"
}
object SinViewModel {
    private val items = mutableListOf<Sin>()

    fun getAll(): List<Sin> = items.toList()

    fun getById(id: String): Sin? = items.find { it.id == id }

    fun exists(id: String): Boolean = items.any { it.id == id }

    fun save(item: Sin): Boolean {
        if (exists(item.id)) return false
        items.add(item)
        return true
    }

    fun update(item: Sin): Boolean {
        val index = items.indexOfFirst { it.id == item.id }
        if (index == -1) return false
        items[index] = item
        return true
    }

    fun delete(id: String): Boolean = items.removeAll { it.id == id }
}