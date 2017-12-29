package us.wmwm.bittrex.main

import us.wmwm.bittrex.app.BittrexApp

fun main(args: Array<String>) {
    val thread = Thread() {
        run {
            val bittrex = BittrexApp()
            bittrex.start()
            System.`in`.read()
        }
    }
    thread.isDaemon = false
    thread.start()
    thread.join()
}