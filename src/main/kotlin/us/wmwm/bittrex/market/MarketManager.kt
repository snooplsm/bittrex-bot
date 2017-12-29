package us.wmwm.bittrex.market

import rx.Observable
import rx.Subscription
import rx.schedulers.Schedulers
import rx.subjects.BehaviorSubject
import us.wmwm.bittrex.api.Api
import us.wmwm.bittrex.models.Market
import us.wmwm.bittrex.models.Markets
import java.util.Comparator
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketManager @Inject constructor(val api: Api) {

    private var marketSub: Subscription? = null
    private var marketApiSub: Subscription? = null
    private var marketPublisher: BehaviorSubject<Markets> = BehaviorSubject.create()

    fun markets(): Observable<Markets> {
        if (marketSub?.isUnsubscribed != false) {
            marketSub = Observable.interval(0, 60000, TimeUnit.MILLISECONDS)
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .subscribe({ _ ->
                        fetchMarket()
                    })
        }
        return marketPublisher.asObservable().doOnUnsubscribe {
            if (!marketPublisher.hasObservers()) {
                marketSub?.unsubscribe()
            }

        }
    }

    private fun fetchMarket() {
        marketApiSub?.unsubscribe()
        marketApiSub = api.markets()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map { res ->
                    if (res.error() != null) {
                        throw res?.error()!!
                    }
                    res.response()?.body()
                }
                .map { res ->
                    val markets =
                            res?.result
                                    ?.filter { it.isActive }
                                    ?.sortedWith(MarketComparator())
                    markets
                }
                .subscribe({ res ->
                    marketPublisher.onNext(Markets(res!!))
                }, { _ ->

                })
    }
}

class MarketComparator : Comparator<Market> {
    override fun compare(o1: Market?, o2: Market?): Int {
        val o1o = o1!!.baseCurrency
        val o2o = o2!!.baseCurrency
        var c: Int = o1o!!.ordinal - o2o!!.ordinal
        if (c != 0) {
            return c
        }
        val o2a = o2.marketName.substringAfter('-')
        val o1a = o1.marketName.substringAfter('-')
        c = o1a.compareTo(o2a)
        return c
    }

}