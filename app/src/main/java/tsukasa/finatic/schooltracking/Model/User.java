package tsukasa.finatic.schooltracking.Model;

import android.net.Uri;

import java.util.HashMap;

public class User {
    private String uid,email;
    private Uri img;
    private HashMap<String,User> acceptList; //List of user Friends

    public  User(){

    }
    //Constructor for user
//    public User(String uid, String email) {
//        this.uid = uid;
//        this.email = email;
//        acceptList = new HashMap<>();
//    }

    public User(String uid, String email, Uri photoUrl) {
    }

    public Uri getImg() {
        return img;
    }

    public void setImg(Uri img) {
        this.img = img;
    }

    //other getter and setter

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public HashMap<String, User> getAcceptList() {
        return acceptList;
    }

    public void setAcceptList(HashMap<String, User> acceptList) {
        this.acceptList = acceptList;
    }
}
