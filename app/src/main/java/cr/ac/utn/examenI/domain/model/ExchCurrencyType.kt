package cr.ac.utn.examenI.domain

/** Currency that the person hands over at the bank.
 * USD means Dollars to colones,
 * CRC means colones to dollars.*/
enum class ExchCurrencyType(val code: String) {
    USD("USD"),
    CRC("CRC");

    /** Currency that the person receives. */
    fun opposite(): ExchCurrencyType = if (this == USD) CRC else USD
    override fun toString(): String = code
}