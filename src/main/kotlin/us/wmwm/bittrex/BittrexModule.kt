package us.wmwm.bittrex;

import dagger.Module
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import com.google.gson.Gson
import okhttp3.OkHttpClient
import javax.inject.Singleton
import dagger.Provides
import us.wmwm.bittrex.api.Api
import java.util.concurrent.TimeUnit
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import us.wmwm.bittrex.api.UtcDateTypeAdapter
import us.wmwm.bittrex.privateapi.PrivateApi
import us.wmwm.bittrex.privateapi.PrivateApiInterceptor
import java.util.*


@Module
class BittrexModule {

    @Provides
    fun okHttpClient(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
    }

    @Provides
    @Singleton
    fun api(client: OkHttpClient.Builder): Api {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://bittrex.com/api/")
                .addConverterFactory(GsonConverterFactory.create(gson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client.build())
                .build()
        return retrofit.create(Api::class.java)
    }

    @Provides
    @Singleton
    fun privateApi(client: OkHttpClient.Builder): PrivateApi {
        client.addInterceptor(PrivateApiInterceptor())
        val retrofit = Retrofit.Builder()
                .baseUrl("https://bittrex.com/api/")
                .addConverterFactory(GsonConverterFactory.create(gson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client.build())
                .build()
        return retrofit.create(PrivateApi::class.java)
    }

    @Provides
    fun gson(): Gson {
        return GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .registerTypeAdapter(object : TypeToken<Date>(){}.type, UtcDateTypeAdapter())
                .create()
    }
}
