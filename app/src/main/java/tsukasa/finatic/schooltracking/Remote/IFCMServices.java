package tsukasa.finatic.schooltracking.Remote;

import io.reactivex.Observable;
import io.reactivex.Observer;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import tsukasa.finatic.schooltracking.Model.MyResponse;
import tsukasa.finatic.schooltracking.Model.Request;

//IFCM1
public interface IFCMServices {
    //Cloud Messaging Key
    @Headers({
        "Content-Type:application/json",
            "Authorization:key=AAAAKy5JJ0o:APA91bHmx0Tc9qxj9J1In8C7qVrOaJWHHHSUqtzNz4MLN7QwSk9apTA8yNNqw60MrW2ARqwRV7VyPBwNupxYTox5XUiFHKqLS6mfTEVJ1te27nuYy4PCKJnkt5LiS7-_WY7k3t1FfhGl"

    })
    @POST("fcm/send")
    Observable<MyResponse> sendFriendRequestToUser(@Body Request body);
}
