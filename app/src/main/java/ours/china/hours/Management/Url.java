package ours.china.hours.Management;

public class Url {
//    public static String baseUrl = "http://192.168.6.222/Hour/";
public static String domainUrl = "http://Hours.worldwardmobi.com";
    public static String baseUrl = "http://Hours.worldwardmobi.com/api/";

    public static String loginUrl = baseUrl + "login_by_oauth";
    public static String faceLogin = baseUrl + "login_by_face";

    public static String verifyCode = baseUrl + "request_account";
    public static String confirmVerify = baseUrl + "register_account";
    public static String register = baseUrl + "register";
    public static String update_profile = baseUrl + "update_profile";

    public static String request_forgot = baseUrl + "request_forgot";
    public static String confirm_forgot = baseUrl + "confirm_forgot";

    public static String get_profile = baseUrl + "get_profile";


    public static String featuredata = baseUrl + "bookUserFaceInfo";
    public static String imageUpload = baseUrl + "bookUserFaceInfo";

    // for search book

    public static String mybooksStatistics = baseUrl + "mybooks_statistics";
    public static String booksStatistics = baseUrl + "books_statistics";
    public static String searchAllBookwithMobile = baseUrl + "query_books";
    public static String searchMyBookwithMobile = baseUrl + "query_mybooks";
    public static String bookStateChangeOperation = baseUrl + "update_bookstatus";

    // login
    public static String getFaceInfoUrl = baseUrl + "getFaceInfoUrl";

    // for face upload
    public static String uploadIdCard = baseUrl + "upload_idcard";
    public static String uploadFaceInfo = baseUrl + "upload_faceinfo";


    public static String addToMybooks = baseUrl + "add_to_mybooks";
    public static String notifyServerBookDownLoaded = baseUrl + "notifyServerBookDownLoaded";

    public static final String CLIENT_ID = "hours_reader";
    public static final String CLIENT_SECRET = "a55b8ca1-c6b5-4867-b0dc-766dfb41d073";
    public static final String SCOPE = "userinfo bookinfo readinfo";
}
