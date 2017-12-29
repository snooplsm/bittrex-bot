package us.wmwm.bittrex.app

import dagger.Component
import us.wmwm.bittrex.BittrexModule
import us.wmwm.bittrex.api.Api
import us.wmwm.bittrex.currency.CurrencyManager
import us.wmwm.bittrex.market.MarketManager
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(BittrexModule::class))
interface Bittrex {
    fun api(): Api
    fun marketManager(): MarketManager
    fun currencyManager(): CurrencyManager
}