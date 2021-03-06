package ch.unstable.lib.sbb.auth

import android.util.Base64
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.Util.UTF_8
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


class AuthInterceptor(private val dateSource: DateSource) : Interceptor {

    constructor() : this(DefaultDateSource())

    private val userAgent = run {
        val versionName = "10.6.0"
        val androidVersion = "9"
        val deviceName = "Google;Android SDK built for x86"
        "SBBmobile/flavorprodRelease-$versionName-RELEASE Android/$androidVersion ($deviceName)"
    }

    private val token = UUID.randomUUID().toString()

    private val macKey: SecretKeySpec = SecretKeySpec(Keys.macKey.toByteArray(UTF_8), "HmacSHA1")

    private fun createSignature(date: String, path: String): String {
        return Mac.getInstance("HmacSHA1").also {
            it.init(macKey)
            it.update(path.toByteArray(UTF_8))
            it.update(date.toByteArray(UTF_8))
        }.doFinal().let { Base64.encodeToString(it, Base64.NO_WRAP) }
    }

    fun createNewRequest(original: Request): Request {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(dateSource.currentDate)
        val url = original.url()

        val signature = createSignature(date, url.encodedPath())

        // for unknown reasons the path needs to be url-encoded again AFTER calculating the
        // signature..
        val doubleEncodedUrl = original.url().newBuilder()
                .encodedPath("/")
                .also {
                    url.encodedPathSegments().forEach {segment ->
                        it.addPathSegment(segment)
                    }
                }.build()

        return original.newBuilder()
                .url(doubleEncodedUrl)
                .addHeader("X-API-AUTHORIZATION", signature)
                .addHeader("X-API-DATE", date)
                .addHeader("X-APP-TOKEN", token)
                .addHeader("User-Agent", userAgent)
                .build()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = createNewRequest(chain.request())
        return chain.proceed(request)
    }
}

