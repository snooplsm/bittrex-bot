package us.wmwm.bittrex.api

import retrofit2.adapter.rxjava.Result
import retrofit2.http.GET
import rx.Single
import us.wmwm.bittrex.models.MarketsResponse

interface Api {

    @GET("v1.1/public/getmarkets")
    fun markets(): Single<Result<MarketsResponse>>

}

