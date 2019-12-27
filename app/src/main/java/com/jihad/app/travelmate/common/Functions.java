package com.jihad.app.travelmate.common;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Functions {

    /*------send the [parameter Activity].Class----------*/
    public static void sendToActivityAsNew(Context context, Class activityClass) {

        Intent loginIntent = new Intent(context, activityClass);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(loginIntent);
    }

    /*------send the [parameter Activity].Class----------*/
    public static void sendToActivity(Context context, Class activityClass) {

        Intent loginIntent = new Intent(context, activityClass);
        context.startActivity(loginIntent);
    }

    public static boolean isEmptySetError(EditText...editText) {

        boolean result = false;

        for (EditText et: editText) {
            if (et.getText().toString().isEmpty()){
                et.setError("Cannot be blank");
                et.requestFocus();
                return true;
            }
        }
        return result;
    }


    public static String currentDateFormat(){

        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");

        return dateFormat.format(calForDate.getTime());
    }

    public static String currentTimeFormat(){

        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");

        return timeFormat.format(calForDate.getTime());
    }

    public static String intToTimeFormat(int time){

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

        return timeFormat.format(time);
    }

    public static int getCurrentDateInt(){

        Calendar start  = Calendar.getInstance();
        int year        = start.get(Calendar.YEAR);
        int month       = start.get(Calendar.MONTH);
        int day         = start.get(Calendar.DAY_OF_MONTH);

        return getDateInt(day, month, year);
    }

    public static int getDateInt(int day, int month, int year){

        month +=1;

        String sMonth = ""+month;
        String sDay = ""+day;
        if (month<10){
            sMonth = "0"+month;
        }
        if (day<10){
            sDay = "0"+day;
        }
        String intDate = ""+year+sMonth+sDay;

        return Integer.parseInt(intDate);
    }

    public static String getTodayName(){
        String dayName = new SimpleDateFormat("EEEE").format(new Date());
        return dayName;
    }


    /*-----------------------*/
    public static String getDayName(int i){

        String[] daysName = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        return daysName[i-1];
    }

    public static String getMonthName(int i){

        String[] monthsName = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return monthsName[i];
    }
    public static String printDateFormat(int day, int month, int year){

        String stringDay = "";
        if (day<10){
            stringDay ="0"+day;
        }else {
            stringDay = ""+day;
        }
        return stringDay+" "+getMonthName(month)+", "+year;
    }



}
