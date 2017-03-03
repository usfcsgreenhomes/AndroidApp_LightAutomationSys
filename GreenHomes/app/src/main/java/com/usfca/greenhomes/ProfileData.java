package com.usfca.greenhomes;

import android.content.SharedPreferences;

public class ProfileData {
    static SharedPreferences pref;
    // In app profile data
    static public String nickname;
    static public String phone;
    static public String userID;
    static public String emailID;
    static public String groups;
    static public String waitInterval;
    static public String lightInterval;
    static public boolean loggedin = false;

    //for shared preference
    static final String PREF_USERNAME = "username";
    static final String PREF_PASSWORD = "password";
    static final String PREF_USERID = "userid";
    static final String PREF_EMAILID = "emailid";
    static final String PREF_NICKNAME = "nickname";
    static final String PREF_PHONE = "phone";
    static final String PREF_GROUPS = "groups";
    static final String PREF_WAITINTERVAL = "waitinterval";
    static final String PREF_LIGHTINTERVAL = "lightinterval";
    static final String PREF_LOGGEDIN = "loggedin";
    static final String PREF_FILE = "GreenHomes";
}
