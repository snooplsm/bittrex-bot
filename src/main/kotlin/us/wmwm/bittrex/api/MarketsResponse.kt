package us.wmwm.bittrex.api

import com.google.gson.annotations.SerializedName
import us.wmwm.bittrex.models.Market

class MarketsResponse : Response() {

    @SerializedName("result")
    var result:List<Market> = emptyList()
}

