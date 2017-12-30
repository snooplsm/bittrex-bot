package us.wmwm.bittrex.privateapi

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import rx.Single
import us.wmwm.bittrex.api.Response

interface PrivateApi {

    @GET("v1.1/account/getbalances")
    fun balances(): Single<BalanceResponse>

}

class BalanceResponse : Response() {
    @SerializedName("result")
    lateinit var result:List<Balance>
}

class Balance {
    lateinit var currency:String
    var balance:Double = 0.0
    var available:Double = 0.0
    var pending:Double = 0.0
    lateinit var cryptoAddress:String
}
