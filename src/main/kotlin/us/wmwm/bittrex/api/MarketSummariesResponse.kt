package us.wmwm.bittrex.api

import com.google.gson.annotations.SerializedName

class MarketSummariesResponse : Response() {

    @SerializedName("result")
    lateinit var result:List<MarketSummary>
}

