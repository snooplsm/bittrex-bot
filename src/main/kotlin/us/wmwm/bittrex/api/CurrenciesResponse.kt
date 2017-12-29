package us.wmwm.bittrex.api

import com.google.gson.annotations.SerializedName
import us.wmwm.bittrex.models.Currency
import us.wmwm.bittrex.models.CurrencyType

class CurrenciesResponse : Response() {

    @SerializedName("result")
    lateinit var result: List<Currency>

}