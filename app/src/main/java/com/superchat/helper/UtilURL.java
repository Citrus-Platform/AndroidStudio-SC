package com.superchat.helper;

import android.support.annotation.NonNull;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by Akshay on 6/18/2015.
 * Modified by: Sumit
 */
public class UtilURL {

    public static String getEncodeURL(@NonNull final String URL){
        String encodeURL = null;

        try {
            encodeURL = URLEncoder.encode(URL, "UTF-8");
        } catch(Exception e){

        }

        return encodeURL;
    }

    public static String getDecodeURL(@NonNull final String URL){
        String decodeURL = null;

        try {
            decodeURL = URLDecoder.decode(URL, "UTF-8");
        } catch(Exception e){

        }

        return decodeURL;
    }

    public static String getProcessedNextURL(@NonNull final String URL){
        String nextURL = null;

        try {
            nextURL = getDecodeURL(URL);
            String strDivider = "?";
            if(nextURL.contains(strDivider)){
                String suffixURL = nextURL.substring(0, nextURL.indexOf(strDivider));
                String prefixURL = nextURL.substring(nextURL.indexOf(strDivider), nextURL.length());

                suffixURL = suffixURL.endsWith("/") ? suffixURL : suffixURL + "/";

                nextURL = suffixURL + prefixURL;
            } else{
                nextURL = nextURL.endsWith("/") ? nextURL : nextURL + "/";
            }
        } catch(Exception e){

        }

        return nextURL;
    }

}
