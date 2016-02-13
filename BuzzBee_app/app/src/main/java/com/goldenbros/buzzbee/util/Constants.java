package com.goldenbros.buzzbee.util;

import java.util.Locale;

/**
 * Created by kimiko on 2015/7/26.
 */
public class Constants {
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // This sample App is for demonstration purposes only.
    // It is not secure to embed your credentials into source code.
    // Please read the following article for getting credentials
    // to devices securely.
    // http://aws.amazon.com/articles/Mobile/4611615499399490
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    public static final String ACCESS_KEY_ID = "key";
    public static final String SECRET_KEY = "key";

    public static final String PICTURE_BUCKET = "buzzbeestorage-user";
    public static final String PICTURE_NAME = "buzzbee-trial";

    public static String getPictureBucket() {
        return ("buzzbee" + ACCESS_KEY_ID + PICTURE_BUCKET).toLowerCase(Locale.US);
    }
}
