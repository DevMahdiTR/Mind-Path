package mindpath.config;

public final class APIRouters {

    public static final String BASE_PATH = "https://devfoknje7ik.com:";
    public static final String PORT = "8081";
    private static final String BASE_API_PATH = "api";
    private static final String CURRENT_VERSION = "v1";
    public static final String AUTH_ROUTER = BASE_API_PATH + "/" + CURRENT_VERSION + "/auth";
    public static final String GROUP_ROUTER = BASE_API_PATH + "/" + CURRENT_VERSION + "/groups";
    public static final String OFFER_ROUTER = BASE_API_PATH + "/" + CURRENT_VERSION + "/offers";
    public static final String SUBJECT_ROUTER = BASE_API_PATH + "/" + CURRENT_VERSION + "/subjects";
    public static final String SUPER_TEACHER_ROUTER = BASE_API_PATH + "/" + CURRENT_VERSION + "/super-teachers";
    public static final String PLAY_LIST_ROUTER = BASE_API_PATH + "/" + CURRENT_VERSION + "/play-lists";
    public static final String CHAT_ROUTER = BASE_API_PATH + "/" + CURRENT_VERSION + "/chat";
    public static final String USER_ROUTER = BASE_API_PATH + "/" + CURRENT_VERSION + "/users";
    public static final String STATS_ROUTER = BASE_API_PATH + "/" + CURRENT_VERSION + "/stats";
    public static final String AZURE_STORAGE_ROUTER  = BASE_API_PATH + "/" + CURRENT_VERSION + "/azure-storage";

    public static String getConfirmationURL(String confirmationToken) {
        return BASE_PATH + PORT +"/" + AUTH_ROUTER + "/confirm?token=" + confirmationToken;
    }

    private APIRouters() {}
}
