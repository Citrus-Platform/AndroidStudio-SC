package com.superchat.helper;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Munish Thakur on 6/18/2015.
 * Modified by: Munish Thakur
 */
public class UtilDateTime {

    //Constants in seconds
    private static final long SECOND = 1;
    private static final long MINUTE = 60;
    private static final long HOUR = 3600;
    private static final long DAY = 86400;
    private static final long MONTH = 2592000;

    /**
     * @param strDateTime in "yyyy-MM-dd'T'HH:mm:ss.SSSZ" format eg 2001-07-04T12:08:56.235-0700
     * @return Time difference between now and datetime passed as param. eq. if passed date time is 2001-07-04T10:08:56.235-0700 and current is 2001-07-04T12:08:56.235-0700, it will return 2h
     */
    public static synchronized String getTimePassed(String strDateTime) throws ParseException {
        try {
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date gmtDate = dateFormatGmt.parse(strDateTime);
            return getTimePassed(gmtDate);
        } catch(Exception e){
            return "";
        }
    }

    /**
     * @param gmtDate Date object
     * @return Time difference between now and datetime passed as param. eq. if passed date time is 2001-07-04T10:08:56.235-0700 and current is 2001-07-04T12:08:56.235-0700, it will return 2h
     * @throws ParseException
     */
    public static synchronized String getTimePassed(Date gmtDate) throws ParseException {
        long diffInMillis = new Date().getTime() - gmtDate.getTime();
        long diffInSeconds = diffInMillis/1000;

        String strDiff = "unknown";
        if(diffInSeconds < 5* SECOND)
            strDiff = "Just Now";
        else if(diffInSeconds < 60* SECOND)
            strDiff = diffInSeconds+" sec ago";
        else if(diffInSeconds < 120* SECOND)
            strDiff = " a min ago";
        else if(diffInSeconds < HOUR)
            strDiff = diffInSeconds/ MINUTE +" min ago";
        else if(diffInSeconds < 2* HOUR)
            strDiff = " an hour ago";
        else if(diffInSeconds < DAY)
            strDiff = diffInSeconds/ HOUR +" hrs ago";
        else if(diffInSeconds < 2* DAY)
            strDiff = " yesterday";
        else if(diffInSeconds <= MONTH)
            strDiff = diffInSeconds/ DAY +" days ago";
        else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(gmtDate);
            Calendar calendarCurrent = Calendar.getInstance();
            calendarCurrent.setTime(new Date());
            if(calendarCurrent.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
                SimpleDateFormat dateFormatWithoutYear = new SimpleDateFormat("d MMM");
                strDiff = dateFormatWithoutYear.format(calendar.getTime());
            } else{
                SimpleDateFormat dateFormatWithoutYear = new SimpleDateFormat("d MMM yyyy");
                strDiff = dateFormatWithoutYear.format(calendar.getTime());
            }
        }
        return strDiff;
    }

    public static String convertToDateStringWithsdf(String unformateddate, SimpleDateFormat sdf) {
        // TODO Auto-generated method stub
        Date date = null;
        String dateStr = unformateddate;
        try {

            date = sdf.parse(unformateddate);
            dateStr = new SimpleDateFormat("dd MMM yyyy").format(date);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return dateStr;
    }


    public static boolean containsDigit(String s){
        boolean containsDigit = false;

        if(s != null && !s.isEmpty()){
            for(char c : s.toCharArray()){
                if(containsDigit = Character.isDigit(c)){
                    break;
                }
            }
        }

        return containsDigit;
    }
    public static Date String2Date(String datestr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(datestr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String setTimeAfter(String dateCreatedString) {

        Calendar now = Calendar.getInstance();
        Calendar day = Calendar.getInstance();
        try {
            day.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(dateCreatedString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String endDateFormated = "";

        Date nowDate = now.getTime();
        Date lastDate = day.getTime();

        endDateFormated = getDateDiffString(nowDate, lastDate);
        Log.e("endDateFormated", "nowDate :" + nowDate + "lastDate :" + lastDate + "after:" + endDateFormated);
        return endDateFormated;

    }
    public static String getDateDiffString(Date lastDate, Date dateTwo) {
        long timeOne = lastDate.getTime();
        long timeTwo = dateTwo.getTime();
        long oneDay = 1000 * 60 * 60 * 24;
        long delta = (timeTwo - timeOne) / oneDay;


       /* long year = delta / 365;
        long rest = delta % 365;
        long month = rest / 30;
        rest = rest % 30;
        long weeks = rest / 7;
        long days = rest % 7;

*/
        if (delta > 0) {
            if (delta <= 29) {
                return delta + " days left";
            } else if (delta >= 30 && delta <= 59) {
                return "1 Month left";
            } else if (delta >= 60 && delta <= 89) {
                return "2 Month left";
            } /*else if (delta >= 90 && delta <= 119) {
                return "3 Month left";
            }*/ else {
                String monthFormat = (String) android.text.format.DateFormat.format("MMM", dateTwo);
                String yearFormat = (String) android.text.format.DateFormat.format("yyyyy", dateTwo);
                String dayFormat = (String) android.text.format.DateFormat.format("dd", dateTwo); //20
                String time = null;

                return dayFormat + "-" + monthFormat + "-" + yearFormat;
            }
        } else {
            delta *= -1;
            if (delta == 0) {
                return "Today";
            } else if (delta == 1) {
                return "Yesterday";
            }else{
                String monthFormat = (String) android.text.format.DateFormat.format("MMM", dateTwo);
                String yearFormat = (String) android.text.format.DateFormat.format("yyyyy", dateTwo);
                String dayFormat = (String) android.text.format.DateFormat.format("dd", dateTwo); //20
                String time = null;

                return dayFormat + "-" + monthFormat + "-" + yearFormat;
            }
        }


    }


}
