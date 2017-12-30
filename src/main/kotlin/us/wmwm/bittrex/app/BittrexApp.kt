package us.wmwm.bittrex.app

import rx.Subscription
import us.wmwm.bittrex.models.Currencies
import us.wmwm.bittrex.models.MarketSummaries
import us.wmwm.bittrex.models.Markets

class BittrexApp() {

    private val app: Bittrex = DaggerBittrex.builder().build()

    private var markets: Markets? = null
    private var currencies: Currencies? = null
    private var marketSummaries: MarketSummaries? = null

    var marketSub: Subscription? = null
    var currencySub: Subscription? = null
    var marketSummarySub: Subscription? = null

    fun start() {
        //startMarketUpdater()
        //startCurrencyUpdater()
        startMarketSummaryUpdater()
    }

    private fun startMarketSummaryUpdater() {
        marketSummarySub?.unsubscribe()
        marketSummarySub = app.marketSummaryManager()
                .marketSummaries()
                .subscribe({res->
                    marketSummaries = res;
                    res.marketSummaries
                            .filter { it ->
                                it.marketName.startsWith("ETH-")
                            }
                            .forEach({
                                //println(it)
                            })
                    if(hasCurrenciesAndMarkets()) {
                        return@subscribe
                    }
                    handleCurrenciesThatChangedDownMoreThanPercent(75..100000)
                    handleCurrenciesThatChangedDownMoreThanPercent(50..75)
                    handleCurrenciesThatChangedDownMoreThanPercent(25..50)
                    handleCurrenciesThatChangedDownMoreThanPercent(10..25)
                },{error-> error.printStackTrace()})
    }

    private fun handleCurrenciesThatChangedDownMoreThanPercent(range:IntRange) {
        val twentyFivePercents = marketSummaries
                ?.marketSummaries!!.filter { it.changeRate24HrPercent() in range}
                .sortedByDescending { it.changeRate24HrPercent() }
        println("There are ${twentyFivePercents.size} currencies with ${range} % drop")
        twentyFivePercents.forEach {
            val marketName = it.marketName.format("%1$10")
            val rate24 = String.format("%.4f", it.changeRate24HrPercent())
            println("${marketName} ${rate24}")
        }
    }

    fun hasCurrenciesAndMarkets(): Boolean {
        return markets != null && currencies!=null
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