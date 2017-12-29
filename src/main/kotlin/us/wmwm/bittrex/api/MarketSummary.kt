package us.wmwm.bittrex.api

import java.util.*

class MarketSummary {

    var updated:Date = Date()
    lateinit var marketName:String
    var high:Double = 0.0
    var low:Double = 0.0
    var volume:Double = 0.0
    var last:Double = 0.0
    var baseVolume:Double = 0.0
    lateinit var timeStamp: Date
    var bid:Double = 0.0
    var ask:Double = 0.0
    var openBuyOrders:Int = 0
    var openSellOrders:Int = 0
    var prevDay:Double = 0.0
    lateinit var created: Date
    lateinit var displayMarketName:String

    fun changeRate24HrComparison() = high - ask


}