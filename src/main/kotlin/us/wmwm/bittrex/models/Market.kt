package us.wmwm.bittrex.models

import java.util.*

class Market {

    var marketCurrency: Currency? = null
        get() {
            if (field == null) {
                return Currency.UNKNOWN
            }
            return field
        }
    lateinit var marketName: String
    var baseCurrency: Currency? = null
        get() {
            if (field == null) {
                return Currency.UNKNOWN
            }
            return field
        }
    var minTradeSize: Double = 0.0
    var isActive = false
    var created: Date? = null


}