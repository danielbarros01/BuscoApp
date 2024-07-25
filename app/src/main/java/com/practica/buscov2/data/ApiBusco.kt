package com.practica.buscov2.data

import com.practica.buscov2.model.busco.Application
import com.practica.buscov2.model.busco.Profession
import com.practica.buscov2.model.busco.ProfessionCategory
import com.practica.buscov2.model.busco.Proposal
import com.practica.buscov2.model.busco.ResponseCreatedId
import com.practica.buscov2.model.busco.auth.LoginToken
import com.practica.buscov2.model.busco.auth.LoginRequest
import com.practica.buscov2.model.busco.auth.RegisterRequest
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.model.busco.Worker
import com.practica.buscov2.util.Constants.Companion.CHANGE_PASSWORD
import com.practica.buscov2.util.Constants.Companion.CONFIRM_PASSWORD_CODE
import com.practica.buscov2.util.Constants.Companion.CONFIRM_REGISTER
import com.practica.buscov2.util.Constants.Companion.ENDPOINT_APPLICATIONS
import com.practica.buscov2.util.Constants.Companion.ENDPOINT_CATEGORIES
import com.practica.buscov2.util.Constants.Companion.ENDPOINT_JOBS
import com.practica.buscov2.util.Constants.Companion.ENDPOINT_LOGIN
import com.practica.buscov2.util.Constants.Companion.ENDPOINT_MY_PROFILE
import com.practica.buscov2.util.Constants.Companion.ENDPOINT_PHOTO
import com.practica.buscov2.util.Constants.Companion.ENDPOINT_PROFESSIONS
import com.practica.buscov2.util.Constants.Companion.ENDPOINT_PROPOSALS
import com.practica.buscov2.util.Constants.Companion.ENDPOINT_REGISTER
import com.practica.buscov2.util.Constants.Companion.ENDPOINT_USERS
import com.practica.buscov2.util.Constants.Companion.ENDPOINT_WORKERS
import com.practica.buscov2.util.Constants.Companion.GOOGLE_LOGIN
import com.practica.buscov2.util.Constants.Companion.RESEND_CODE
import com.practica.buscov2.util.Constants.Companion.SEND_CODE
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiBusco {
    @POST("$ENDPOINT_USERS/$ENDPOINT_LOGIN")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginToken>

    @POST("$ENDPOINT_USERS/$ENDPOINT_REGISTER")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): Response<LoginToken>

    @GET("$ENDPOINT_USERS/$ENDPOINT_MY_PROFILE")
    suspend fun getMyProfile(@Header("Authorization") token: String): Response<User>

    @GET("$ENDPOINT_USERS/{id}")
    suspend fun getProfile(@Path("id") id: Int): Response<User>

    @PATCH("$ENDPOINT_USERS/$CONFIRM_REGISTER")
    suspend fun confirmRegister(
        @Header("Authorization") token: String,
        @Body body: Map<String, Int>
    ): Response<Unit>

    @FormUrlEncoded
    @PATCH("$ENDPOINT_USERS/$CONFIRM_PASSWORD_CODE")
    suspend fun confirmCodeForPassword(
        @Field("email") email: String,
        @Field("code") code: Int
    ): Response<LoginToken>

    /*
    * resend code se usa si tenemos el token del usuario logueado
    * send code se usa si no tenemos un token del usuario
    *  */

    @PATCH("$ENDPOINT_USERS/$RESEND_CODE")
    suspend fun resendCode(
        @Header("Authorization") token: String
    ): Response<Unit>

    @FormUrlEncoded
    @PATCH("$ENDPOINT_USERS/$SEND_CODE")
    suspend fun sendCode(
        @Field("email") email: String
    ): Response<Unit>

    /* - - */

    @PUT(ENDPOINT_USERS)
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Body user: User
    ): Response<Unit>

    @POST(GOOGLE_LOGIN)
    suspend fun googleLogin(@Body user: User): Response<LoginToken>

    @FormUrlEncoded
    @PATCH("$ENDPOINT_USERS/$CHANGE_PASSWORD")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Field("password") password: String
    ): Response<Unit>

    @Multipart
    @PATCH("$ENDPOINT_USERS/$ENDPOINT_MY_PROFILE/$ENDPOINT_PHOTO")
    suspend fun updatePhoto(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): Response<Unit>


    @GET("$ENDPOINT_PROFESSIONS/{id}")
    suspend fun getProfession(@Path("id") id: Int): Response<Profession>

    @GET("$ENDPOINT_PROFESSIONS/$ENDPOINT_CATEGORIES/{categoryId}")
    suspend fun getProfessionsForCategory(@Path("categoryId") categoryId: Int): Response<ProfessionCategory>

    @GET("$ENDPOINT_PROFESSIONS/search")
    suspend fun getProfessionsSearch(@Query("query") query: String): Response<List<Profession>>

    @GET("$ENDPOINT_PROFESSIONS/$ENDPOINT_CATEGORIES")
    suspend fun getCategories(): Response<List<ProfessionCategory>>

    @POST(ENDPOINT_WORKERS)
    suspend fun addWorker(
        @Header("Authorization") token: String,
        @Body worker: Worker
    ): Response<Unit>

    @GET("$ENDPOINT_WORKERS/{id}")
    suspend fun getWorker(@Path("id") id: Int): Response<Worker>

    @PUT(ENDPOINT_WORKERS)
    suspend fun updateWorker(
        @Header("Authorization") token: String,
        @Body worker: Worker
    ): Response<Unit>

    @GET("$ENDPOINT_WORKERS/recommendations")
    suspend fun getRecommendedWorkers(
        @Header("Authorization") token: String,
        @Query("page") page: Int?,
        @Query("NumberRecordsPerPage") pageSize: Int?,
    ): Response<List<User>>

    @Multipart
    @POST(ENDPOINT_PROPOSALS)
    suspend fun createProposal(
        @Header("Authorization") token: String,
        @Part("title") title: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("requirements") requirements: RequestBody?,
        @Part("minBudget") minBudget: RequestBody?,
        @Part("maxBudget") maxBudget: RequestBody?,
        @Part image: MultipartBody.Part?,
        @Part("professionId") professionId: RequestBody?
    ): Response<ResponseCreatedId>

    @GET("$ENDPOINT_PROPOSALS/all/{userId}")
    suspend fun getProposalsOfUser(
        @Path("userId") userId: Int,
        @Query("page") page: Int?,
        @Query("NumberRecordsPerPage") pageSize: Int?,
        @Query("status") status: Boolean?
    ): Response<List<Proposal>>

    @GET("$ENDPOINT_PROPOSALS/{id}")
    suspend fun getProposal(
        @Path("id") id: Int
    ): Response<Proposal>

    @PATCH("$ENDPOINT_PROPOSALS/{id}/finalize")
    suspend fun finalizeProposal(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>

    @DELETE("$ENDPOINT_PROPOSALS/{id}")
    suspend fun deleteProposal(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>

    @Multipart
    @PUT("$ENDPOINT_PROPOSALS/{id}")
    suspend fun editProposal(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Part("title") title: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("requirements") requirements: RequestBody?,
        @Part("minBudget") minBudget: RequestBody?,
        @Part("maxBudget") maxBudget: RequestBody?,
        @Part image: MultipartBody.Part?,
        @Part("professionId") professionId: RequestBody?
    ): Response<Unit>

    @GET("$ENDPOINT_PROPOSALS/recommendations")
    suspend fun getRecommendedProposals(
        @Header("Authorization") token: String,
        @Query("page") page: Int?,
        @Query("NumberRecordsPerPage") pageSize: Int?,
    ): Response<List<Proposal>>

    @GET("$ENDPOINT_APPLICATIONS/{proposalId}")
    suspend fun getApplicationsForProposal(
        @Header("Authorization") token: String,
        @Path("proposalId") proposalId: Int,
        @Query("page") page: Int?,
        @Query("NumberRecordsPerPage") pageSize: Int?
    ): Response<List<Application>>

    @GET("$ENDPOINT_APPLICATIONS/{proposalId}/accepted")
    suspend fun getApplicationAccepted(
        @Path("proposalId") proposalId: Int
    ): Response<Application>

    @PATCH("$ENDPOINT_APPLICATIONS/{proposalId}/{applicationId}")
    suspend fun acceptOrDeclineApplication(
        @Header("Authorization") token: String,
        @Path("proposalId") proposalId: Int,
        @Path("applicationId") applicationId: Int,
        @Query("status") status: Boolean? //TRUE ACEPTAR, FALSE RECHAZAR
    ): Response<Unit>

    @POST("$ENDPOINT_APPLICATIONS/{proposalId}")
    suspend fun createApplication(
        @Header("Authorization") token: String,
        @Path("proposalId") proposalId: Int,
    ):Response<Unit>

    @GET(ENDPOINT_JOBS)
    suspend fun getJobs(
        @Header("Authorization") token: String,
        @Query("page") page: Int?,
        @Query("NumberRecordsPerPage") pageSize: Int?,
        @Query("finished") finished: Boolean?,
    ): Response<List<Proposal>>
}