package us.wmwm.bittrex.privateapi

import okhttp3.Interceptor
import okhttp3.Response
import java.io.File
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SignatureException
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class PrivateApiInterceptor : Interceptor {

    companion object {
        val API_KEY = "apikey"
        val NONCE = "nonce"
        val API_SIGN = "apisign"
    }
    val key:String
    val secret:String

    init {
        val properties = Properties()
        properties.load(
                File("${System.getProperty("user.home")}${File.separator}.bittrex${File.separator}.keys").inputStream())
        key = properties.getProperty("key")
        secret = properties.getProperty("secret")
    }

    private val HMAC_SHA512 = "HmacSHA512"

    private fun toHexString(bytes: ByteArray): String {
        val formatter = Formatter()
        for (b in bytes) {
            formatter.format("%02x", b)
        }
        return formatter.toString()
    }

    @Throws(SignatureException::class, NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun calculateHMAC(data: String, key: String): String {
        val secretKeySpec = SecretKeySpec(key.toByteArray(), HMAC_SHA512)
        val mac = Mac.getInstance(HMAC_SHA512)
        mac.init(secretKeySpec)
        return toHexString(mac.doFinal(data.toByteArray()))
    }

    override fun intercept(chain: Interceptor.Chain?): Response {
        val builder = chain!!.request().newBuilder()
        val nonce = System.currentTimeMillis()
        val uri = chain.request().url()

        val uribuilder = uri.newBuilder().addQueryParameter(API_KEY,key)
                .addQueryParameter(NONCE, java.lang.String.valueOf(nonce))
        val uriString = uribuilder.build().toString()
        builder.addHeader(API_SIGN, calculateHMAC(uriString, secret))
        return chain.proceed(builder.build())
    }
}