package us.wmwm.bittrex.api

import retrofit2.adapter.rxjava.Result
import retrofit2.http.GET
import rx.Single

interface Api {

    @GET("v1.1/public/getmarkets")
    fun markets(): Single<Result<MarketsResponse>>

    @GET("v1.1/public/getcurrencies")
    fun currencies(): Single<Result<CurrenciesResponse>>

}

