package cr.ac.utn.examenI.domain.model
import cr.ac.utn.examenI.domain.ExchCurrencyType
import java.time.LocalDateTime

/** Person that performs a currency exchange. It inherits the person data
 * (id, name, last names, phone, email and address) from [Person].*/
class ExchTransaction(
    id: String,
    name: String,
    firstName: String,
    lastName: String,
    phone: Int,
    email: String,
    address: String,
    val exchangeDateTime: LocalDateTime,
    val exchangeRate: Double,
    val amountToExchange: Double,
    val currencyType: ExchCurrencyType,
    val bank: ExchBank
) : Person(
    id = id,
    name = name,
    firstName = firstName,
    lastName = lastName,
    phone = phone,
    email = email,
    address = address
) {
    /** Amount the person receives, expressed in [targetCurrency]. */
    val totalAmountToPay: Double
        get() = calculateTotal(exchangeRate, amountToExchange, currencyType)

    val targetCurrency: ExchCurrencyType
        get() = currencyType.opposite()

    companion object {
        /** USD to CRC multiplies by the rate, CRC to USD divides by the rate.
         * The result is rounded to two decimals.*/
        fun calculateTotal(rate: Double, amount: Double, currencyType: ExchCurrencyType): Double {
            if (rate <= 0.0 || amount <= 0.0) return 0.0
            val total = when (currencyType) {
                ExchCurrencyType.USD -> amount * rate
                ExchCurrencyType.CRC -> amount / rate
            }
            return Math.round(total * 100) / 100.0
        }
    }
}