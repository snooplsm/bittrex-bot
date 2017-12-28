package us.wmwm.bittrex.models

import com.google.gson.annotations.SerializedName

class MarketsResponse : Response() {

    @SerializedName("result")
    lateinit var result:List<Market>
}

