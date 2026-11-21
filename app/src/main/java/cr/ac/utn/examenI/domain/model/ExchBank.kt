package cr.ac.utn.examenI.domain.model
/** Banks available to perform the exchange.*/
enum class ExchBank(val code: String) {
    BCR("BCR"),
    BNC("BNC"),
    BAC("BAC");

    override fun toString(): String = code
}
