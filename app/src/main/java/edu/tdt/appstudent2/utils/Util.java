package edu.tdt.appstudent2.utils;

import java.util.Calendar;

import edu.tdt.appstudent2.models.TietHoc;

/**
 * Created by Bichan on 7/15/2016.
 */
public class Util {
    public static final int XEPLOAI_GOI = 0;
    public static final int XEPLOAI_KHA = 1;
    public static final int XEPLOAI_TB = 2;
    public static final int XEPLOAI_YEU = 3;



    public static String showCalendar(Calendar c) {
        int year = c.get(Calendar.YEAR);

        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        int millis = c.get(Calendar.MILLISECOND);
        month++;
        return "" + day + " tháng " + month + ", " + year;
    }

    public static String getThuCalendar(Calendar c){
        String thu = "";
        int thuInt = c.get(Calendar.DAY_OF_WEEK);
        if(thuInt == 1)
            thu = "Chủ nhật";
        else
            thu = "Thứ " + thuInt;
        return thu;
    }

    public static int xepLoaiDiem(String diem){
        try {
            double n = Double.parseDouble(diem);
            if(n >= 8 && n <= 10){
                return XEPLOAI_GOI;
            }else if (n >= 6.5 && n < 8){
                return XEPLOAI_KHA;
            }else
                return XEPLOAI_TB;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static String xeLoaiDiemColor(String diem){
        try {
            double n = Double.parseDouble(diem);
            if(n >= 8 && n <= 10){
                return  "#88C90D";
            }else if (n >= 6.5 && n < 8) {
                return "#5E78B7";
            }else if (n >= 5 && n < 6.5){
                return "#ECC83B";
            }else
                return "#CB341F";
        } catch (NumberFormatException e) {
            switch (diem){
                case "F":
                    return "#CB341F";
                case "M":
                    return  "#88C90D";
            }
        }
        return "#ECC83B";
    }

    public static String tinhCaHoc(String a){
        String[] tietArray = a.split("");
        String tiet = "";
        for(int i = 1 ; i <= 16; i++){
            if(!tietArray[i].equals("-")){
                tiet = Integer.toString(i);
                break;
            }
        }
        int tietInt = Integer.parseInt(tiet);
        switch (tietInt){
            case 1:
            case 2:
            case 3:
                return "1";
            case 4:
            case 5:
            case 6:
                return "2";
            case 7:
            case 8:
            case 9:
                return "3";
            case 10:
            case 11:
            case 12:
                return "4";
            case 13:
            case 14:
            case 15:
            case 16:
                return "5";
        }
        return "5";
    }

    public static String tinhTGBatDau(String a){
        String[] tietArray = a.split("");
        String tiet = "";
        for(int i = 1 ; i <= 16; i++){
            if(!tietArray[i].equals("-")){
                tiet = Integer.toString(i);
                break;
            }
        }
        for (int i = 0; i <= TietHoc.tietHocs.length; i++){
            if(TietHoc.tietHocs[i].getTen().equals(tiet)){
                return TietHoc.tietHocs[i].getTgBatDau();
            }
        }
        return "";
    }

    public static String tinhTGKetThuc(String a){
        String[] tietArray = a.split("");
        String tiet = "";
        for(int i = 1 ; i <= 16; i++){
            if(!tietArray[i].equals("-")){
                tiet = Integer.toString(i);
            }
        }
        for (int i = 0 ; i < TietHoc.tietHocs.length; i++){
            if(TietHoc.tietHocs[i].getTen().equals(tiet)){
                return TietHoc.tietHocs[i].getTgKetThuc();
            }
        }
        return "";
    }
    
}
