package us.wmwm.bittrex.app

class BittrexApp() {

    val app:Bittrex;

    init {
        app =DaggerBittrex.builder().build()
    }

}