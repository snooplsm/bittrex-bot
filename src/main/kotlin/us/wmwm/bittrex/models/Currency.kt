package us.wmwm.bittrex.models

class Currency {
    lateinit var currency: String
    lateinit var currencyLong: String
    var minConfirmation: Int = 0
    var txFee: Double = Double.MAX_VALUE
    var isActive: Boolean = false
    var coinType: CoinType? = null
        get() {
            if (field == null) {
                return CoinType.UNKNOWN
            }
            return field
        }
    lateinit var notice: String
}