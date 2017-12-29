package us.wmwm.bittrex.app

import rx.Subscription
import us.wmwm.bittrex.models.Currencies
import us.wmwm.bittrex.models.Markets

class BittrexApp() {

    private val app: Bittrex = DaggerBittrex.builder().build()

    private lateinit var markets: Markets
    private lateinit var currencies: Currencies

    var marketSub: Subscription? = null
    var currencySub: Subscription? = null

    fun start() {
        startMarketUpdater()
        startCurrencyUpdater()
    }

    private fun startMarketUpdater() {
        marketSub?.unsubscribe()
        marketSub = app.marketManager()
                .markets()
                .subscribe({ res ->
                    markets = res
                    onMarkets()
                }, { _ ->

                })

    }

    private fun onMarkets() {
        //TODO: FIND NEW MARKETS TO GET IN AT IPO PRICES
    }

    private fun startCurrencyUpdater() {
        currencySub?.unsubscribe()
        currencySub = app.currencyManager()
                .currencies()
                .subscribe({ res ->
                    currencies = res
                }, { _ ->
                })
    }
}