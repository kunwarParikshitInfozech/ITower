import com.isl.leaseManagement.dataClass.requests.FetchDeviceIDRequest
import com.isl.leaseManagement.dataClass.requests.StartTaskRequest
import com.isl.leaseManagement.dataClass.requests.SubmitTaskRequest
import com.isl.leaseManagement.dataClass.requests.UploadDocumentRequest
import com.isl.leaseManagement.dataClass.responses.ApiSuccessFlagResponse
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
        @Path("userId") userId: Int,
        @Query("requestStatus") requestStatus: String?,
        @Query("taskStatus") taskStatus: String?,
        @Query("SLAStatus") slaStatus: String?,
        @Query("requestPriority") requestPriority: String?,
    ): Observable<List<TaskResponse>>

    @GET("leasemanagement/1/users/123/tasks/summary")
    fun getTasksSummary(): Observable<List<TasksSummaryResponse>>

    @Headers("Content-Type: application/json")
    @PUT("leasemanagement/leasemanagement/1/users/123/tasks/{taskId}/status/{taskStatus}")
    fun updateTaskStatus(
        @Path("taskId") taskId: Int,
        @Path("taskStatus") taskStatus: String,
        @Body body: RequestBody
    ): Observable<ApiSuccessFlagResponse>

    @Headers("Content-Type: application/json")
    @PUT("leasemanagement/1/users/123/tasks/{taskId}/start")
    fun startTask(
        @Path("taskId") taskId: Int,
        @Body body: StartTaskRequest
    ): Observable<StartTaskResponse>

    @Headers("Content-Type: application/json")
    @PUT("leasemanagement/1/users/123/tasks/{taskId}/submit")
    fun submitTask(
        @Path("taskId") taskId: Int,
        @Body body: SubmitTaskRequest
    ): Observable<ApiSuccessFlagResponse>

    @GET("leasemanagement/1/users/123/request/{requestId}")
    fun getTaskRequestDetails(@Path("requestId") requestId: String): Observable<RequestDetailsResponse>

    @POST("leasemanagement/1/tasks/{taskId}/documents")
    fun uploadDocument(
        @Path("taskId") taskId: Int,
        @Body body: UploadDocumentRequest
    ): Observable<UploadDocumentResponse>

    @POST("usertracking/1/users/deviceID")
    fun fetchDeviceID(
        @Body body: FetchDeviceIDRequest
    ): Observable<FetchDeviceIDRequest>   //response is also same

}