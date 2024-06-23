import okhttp3.Interceptor
import okhttp3.Response

class ApiInterceptor : Interceptor {

    private val headerAuthToken = "Authorization"

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
//        val authToken = ""
//        authToken?.let {
//            requestBuilder.addHeader(headerAuthToken, "Token $authToken")
//        }
        // build request
        val originalRequest = requestBuilder.build()
        return chain.proceed(originalRequest)
    }
}