//make the "Register for Selected Section" button work. To do this, your application needs to know which student is logged in. You can't just pass the studentId from frame to frame, it's too messy. The best way is to create a static session class that holds the logged-in user's info. Step A: Create UserSession.javaT his class will "remember" who is logged in.
package edu.univ.erp.auth;

// A simple static class to hold the logged-in user's info
public class UserSession {

    private static int userId;
    private static String username;
    private static String role;

    public static void createSession(int id, String name, String r) {
        userId = id;
        username = name;
        role = r;
    }

    public static void clearSession() {
        userId = 0;
        username = null;
        role = null;
    }

    public static int getUserId() { return userId; }
    public static String getUsername() { return username; }
    public static String getRole() { return role; }
}