import com.isl.leaseManagement.dataClass.responses.TaskResponse
import com.isl.leaseManagement.dataClass.responses.TasksSummaryResponse
import io.reactivex.Observable
import retrofit2.http.*

interface IApiRequest {
    @GET("leasemanagement/1/users/123/tasks")
    fun getTasks(): Observable<List<TaskResponse>>

    @GET("leasemanagement/1/users/123/tasks/summary")
    fun getTasksSummary(): Observable<List<TasksSummaryResponse>>
}