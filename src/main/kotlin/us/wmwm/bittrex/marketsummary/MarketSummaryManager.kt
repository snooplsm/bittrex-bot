package us.wmwm.bittrex.marketsummary

import rx.Observable
import rx.Subscription
import rx.schedulers.Schedulers
import rx.subjects.BehaviorSubject
import us.wmwm.bittrex.api.Api
import us.wmwm.bittrex.models.MarketSummaries
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketSummaryManager @Inject constructor(val api: Api) {

    private var marketSummarySub: Subscription? = null
    private var marketSummaryApiSub: Subscription? = null
    private var marketSummaryPublisher: BehaviorSubject<MarketSummaries> = BehaviorSubject.create()

    fun marketSummaries(): Observable<MarketSummaries> {
        if (marketSummarySub?.isUnsubscribed != false) {
            marketSummarySub = Observable.interval(0, 59000, TimeUnit.MILLISECONDS)
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .subscribe({ _ ->
                        fetchMarketSummaries()
                    })
        }
        return marketSummaryPublisher.asObservable().doOnUnsubscribe {
            if (!marketSummaryPublisher.hasObservers()) {
                marketSummarySub?.unsubscribe()
            }

        }
    }

    private fun fetchMarketSummaries() {
        marketSummaryApiSub?.unsubscribe()
        marketSummaryApiSub = api.marketSummaries()
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
                    res?.result
                            ?.sortedWith(MarketRateSummaryComparator())
                }
                .subscribe({ res ->
                    marketSummaryPublisher.onNext(MarketSummaries(res!!))
                }, { _ ->

                })
    }
}