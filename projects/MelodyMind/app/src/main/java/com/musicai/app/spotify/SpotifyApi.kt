package com.musicai.app.spotify

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/* ---------- DATA MODELS ---------- */

data class SpotifyTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int
)

data class SpotifySearchResponse(
    val tracks: TracksContainer?
)

data class TracksContainer(
    val items: List<SpotifyTrack>
)

data class SpotifyTrack(
    val id: String,
    val name: String,
    val preview_url: String?,
    val artists: List<SpotifyArtist>,
    val album: SpotifyAlbum
)

data class SpotifyArtist(val id: String, val name: String)

data class SpotifyAlbum(
    val id: String,
    val name: String,
    val images: List<SpotifyImage>
)

data class SpotifyImage(
    val url: String,
    val width: Int,
    val height: Int
)

/* ---------- RETROFIT SERVICES ---------- */

interface SpotifyAuthService {
    @FormUrlEncoded
    @POST("api/token")
    suspend fun getToken(
        @Header("Authorization") authHeader: String,
        @Field("grant_type") grantType: String = "client_credentials"
    ): SpotifyTokenResponse
}

interface SpotifySearchService {
    @GET("v1/search")
    suspend fun searchTracks(
        @Header("Authorization") bearer: String,
        @Query("q") query: String,
        @Query("type") type: String = "track",
        @Query("limit") limit: Int = 20
    ): SpotifySearchResponse
}

/* ---------- CLIENT IMPLEMENTATION ---------- */

class SpotifyApi(
    private val clientId: String,
    private val clientSecret: String
) {

    // simple in-memory cache keyed by query string
    private val searchCache = mutableMapOf<String, List<SpotifyTrack>>()

    private val http by lazy {
        val log = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        OkHttpClient.Builder().addInterceptor(log).build()
    }

    private val retrofitAuth by lazy {
        Retrofit.Builder()
            .baseUrl("https://accounts.spotify.com/")
            .client(http)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyAuthService::class.java)
    }

    private val retrofitApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.spotify.com/")
            .client(http)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifySearchService::class.java)
    }

    private suspend fun bearerToken(): String = withContext(Dispatchers.IO) {
        val creds = Credentials.basic(clientId, clientSecret)
        val token = retrofitAuth.getToken(creds)
        "${token.tokenType} ${token.accessToken}"
    }

    suspend fun search(query: String, limit: Int = 20): List<SpotifyTrack> =
        withContext(Dispatchers.IO) {
            // return cached copy if present
            searchCache[query]?.let { return@withContext it }

            val token = bearerToken()
            val results = retrofitApi.searchTracks(token, query, "track", limit).tracks?.items ?: emptyList()
            // cache result for offline reuse during session
            searchCache[query] = results
            results
        }
}
