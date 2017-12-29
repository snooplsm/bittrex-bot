package us.wmwm.bittrex.models

import java.util.*

class Market {

    var marketCurrency: CurrencyType? = null
        get() {
            if (field == null) {
                return CurrencyType.UNKNOWN
            }
            return field
        }
    lateinit var marketName: String
    var baseCurrency: CurrencyType? = null
        get() {
            if (field == null) {
                return CurrencyType.UNKNOWN
            }
            return field
        }
    var minTradeSize: Double = 0.0
    var isActive = false
    var created: Date? = null
    fun asString(): String {
        return marketName;
    }


}