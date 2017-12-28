package us.wmwm.bittrex.app

import dagger.Component
import us.wmwm.bittrex.BittrexModule
import us.wmwm.bittrex.api.Api
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(BittrexModule::class))
interface Bittrex {
    fun api():Api
}