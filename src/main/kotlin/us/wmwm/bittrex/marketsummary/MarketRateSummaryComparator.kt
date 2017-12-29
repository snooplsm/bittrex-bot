package us.wmwm.bittrex.marketsummary

import us.wmwm.bittrex.api.MarketSummary
import java.util.Comparator

class MarketRateSummaryComparator : Comparator<MarketSummary> {
    override fun compare(o1: MarketSummary?, o2: MarketSummary?): Int {
        val o1o = o1!!.changeRate24HrComparison()
        val o2o = o2!!.changeRate24HrComparison()
        return o1o.compareTo(o2o)
    }
}