import com.isl.leaseManagement.dataClass.requests.FetchDeviceIDRequest
import com.isl.leaseManagement.dataClass.requests.StartTaskRequest
import com.isl.leaseManagement.dataClass.requests.SubmitTaskRequest
import com.isl.leaseManagement.dataClass.requests.UploadDocumentRequest
import com.isl.leaseManagement.dataClass.responses.ApiSuccessFlagResponse
import com.isl.leaseManagement.dataClass.responses.FetchUserIdResponse
import com.isl.leaseManagement.dataClass.responses.RequestDetailsResponse
import com.isl.leaseManagement.dataClass.responses.StartTaskResponse
import com.isl.leaseManagement.dataClass.responses.TaskResponse
import com.isl.leaseManagement.dataClass.responses.TasksSummaryResponse
import com.isl.leaseManagement.dataClass.responses.UploadDocumentResponse
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.*

interface IApiRequest {

    @GET("leasemanagement/1/users/{userId}/tasks")
    fun getTasks(
        @Path("userId") userId: String,
        @Query("requestStatus") requestStatus: String?,
        @Query("taskStatus") taskStatus: String?,
        @Query("SLAStatus") slaStatus: String?,
        @Query("requestPriority") requestPriority: String?,
    ): Observable<List<TaskResponse>>

    @GET("leasemanagement/1/users/{userId}/tasks/summary")
    fun getTasksSummary(@Path("userId") userId: String): Observable<List<TasksSummaryResponse>>

    @Headers("Content-Type: application/json")
    @PUT("leasemanagement/1/users/{userId}/tasks/{taskId}/status/{taskStatus}")
    fun updateTaskStatus(
        @Path("userId") userId: String,
        @Path("taskId") taskId: Int,
        @Path("taskStatus") taskStatus: String,
        @Body body: RequestBody
    ): Observable<ApiSuccessFlagResponse>

    @Headers("Content-Type: application/json")
    @PUT("leasemanagement/1/users/{userId}/tasks/{taskId}/start")
    fun startTask(
        @Path("userId") userId: String,
        @Path("taskId") taskId: Int,
        @Body body: StartTaskRequest
    ): Observable<StartTaskResponse>

    @Headers("Content-Type: application/json")
    @PUT("leasemanagement/1/users/{userId}/tasks/{taskId}/submit")
    fun submitTask(
        @Path("userId") userId: String,
        @Path("taskId") taskId: Int,
        @Body body: SubmitTaskRequest
    ): Observable<ApiSuccessFlagResponse>

    @GET("leasemanagement/1/users/{userId}/request/{requestId}")
    fun getTaskRequestDetails(
        @Path("userId") userId: String,
        @Path("requestId") requestId: String
    ): Observable<RequestDetailsResponse>

    @POST("leasemanagement/1/tasks/{taskId}/documents")
    fun uploadDocument(
        @Path("taskId") taskId: Int,
        @Body body: UploadDocumentRequest
    ): Observable<UploadDocumentResponse>

    @POST("usertracking/1/users/deviceId")
    fun fetchDeviceID(
        @Body body: FetchDeviceIDRequest
    ): Observable<FetchUserIdResponse>   //response is also same

}