package tsukasa.finatic.schooltracking.utils;

import java.net.URI;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Retrofit;
import tsukasa.finatic.schooltracking.Model.User;
import tsukasa.finatic.schooltracking.Remote.IFCMServices;
import tsukasa.finatic.schooltracking.Remote.RetrofitClient;

public class Common {

    public static final String USER_INFORMATION = "UserInformation";
    public static final String USER_UID_SAVE_KEY = "SaveUID";
    public static final String TOKENS = "Tokens";
    public static final String FROM_NAME = "FromName";
    public static final String ACCEPT_LIST = "acceptList";
    public static final String FROM_UID = "FromUID";
    public static final String TO_UID = "ToUid";
    public static final String TO_NAME = "ToName";
    public static final URI Image = URI.create("image");
    public static final String FRIEND_REQUEST = "FriendRequest";
    public static final String PUBLIC_LOCATION = "Public Location";
    public static User loggedUser;
    public static User trackingUser;

    public static IFCMServices getFCMService(){
        return RetrofitClient.getClient("https://fcm.googleapis.com/")
                .create(IFCMServices.class);
    }

    public static Date convertTimeStampToDate(long time) {
        return new Date(new Timestamp(time).getTime());

    }

    public static String getDateFormatted(Date date) {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm").format(date).toString();
    }
}
