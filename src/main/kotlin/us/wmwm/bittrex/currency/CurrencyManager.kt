package us.wmwm.bittrex.currency

import rx.Observable
import rx.Subscription
import rx.schedulers.Schedulers
import rx.subjects.BehaviorSubject
import us.wmwm.bittrex.api.Api
import us.wmwm.bittrex.models.Currencies
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyManager @Inject constructor(val api: Api) {

    private var currencySub: Subscription? = null
    private var currencyApiSub: Subscription? = null
    private var currencyPublisher: BehaviorSubject<Currencies> = BehaviorSubject.create()

    fun currencies(): Observable<Currencies> {
        if (currencySub?.isUnsubscribed != false) {
            currencySub = Observable.interval(0, 60000, TimeUnit.MILLISECONDS)
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .subscribe({ _ ->
                        fetchCurrencies()
                    })
        }
        return currencyPublisher.asObservable().doOnUnsubscribe {
            if (!currencyPublisher.hasObservers()) {
                currencySub?.unsubscribe()
            }

        }
    }

    private fun fetchCurrencies() {
        currencyApiSub?.unsubscribe()
        currencyApiSub = api.currencies()
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
                    val currencies =
                            res?.result?.filter { it.isActive }
                    currencies

                }
                .subscribe({ res ->
                    currencyPublisher.onNext(Currencies(res!!))
                }, { _ ->

                })
    }
}