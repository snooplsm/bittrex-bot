package us.wmwm.bittrex.app

import rx.Subscription
import us.wmwm.bittrex.models.Markets

class BittrexApp() {

    private val app: Bittrex = DaggerBittrex.builder().build()

    private lateinit var markets: Markets

    var marketSub: Subscription? = null

    fun start() {
        startMarketUpdater();
    }

    private fun startMarketUpdater() {
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
}