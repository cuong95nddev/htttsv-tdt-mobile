package edu.tdt.appstudent2.api;

/**
 * Created by Bichan on 7/14/2016.
 */
public class Api {
    public static final String host = "http://api.itex.cf/v2.php";
    //Link api lay avatar
    public static String apiAvatar(String user, String pass){
        return host
                + "user=" + user
                + "&pass=" + pass
                + "&act=avatar";
    }

    /**
     * link api lấy danh sách đơn vị
     * @return
     */
    public static String apiThongBaoDonVi(String user, String pass){
        return host + "user=" + user
                + "&pass=" + pass
                + "&act=tb";
    }

    /**
     * Link api lấy danh sách thông báo theo đơn vị
     * @param lv id đơn vị
     * @param page trang cần lấy
     * @return
     */
    public static String thongBaoApi(String user, String pass, String lv, int page){
        return host + "user=" + user
                + "&pass=" + pass
                + "&act=tb&lv="+ lv +"&page=" + page;
    }

    /**
     * Link api xem thông báo
     * @param id id thông báo
     * @return
     */
    public static String apiThongBaoXem(String user, String pass, String id){
        return host + "user=" + user
                + "&pass=" + pass
                + "&act=tb&id=" + id;
    }

    /**
     * Lấy danh sách học kỳ học phi
     * @return
     */
    public static String apiHocphiHocKy(String user, String pass){
        return host + "user=" + user
                + "&pass=" + pass
                + "&act=hp&option=lhk";
    }

    /**
     * Lấy Thông tin học phí
     * @param id
     * @return
     */
    public static String apiChiTietHocPhi(String user, String pass, String id){
        return host + "user=" + user
                + "&pass=" + pass
                + "&act=hp&option=lcthp&id=" + id;
    }

    /**
     * Lấy thông tin sinh viên
     * @param user
     * @param pass
     * @return
     */
    public static String apiThongTin(String user, String pass){
        return host + "user=" + user
                + "&pass=" + pass
                + "&act=ttsv";
    }

    /**
     * Lấy diểm tổng hợp
     * @param user
     * @param pass
     * @return
     */
    public static String apiDiemTongHop(String user, String pass){
        return host + "user=" + user
                + "&pass=" + pass
                + "&act=kqht&option=lth";
    }

    public static String apiDiem(String user, String pass, String id){
        return host + "user=" + user
                + "&pass=" + pass
                + "&act=kqht&option=lkq&nametable=" + id;
    }

    public static String apiDiemHocKy(String user, String pass){
        return host + "user=" + user
                + "&pass=" + pass
                + "&act=kqht&option=lhk";
    }

    /**
     * Lấy danh sách học kỳ thời khóa biểu
     * @param user
     * @param pass
     * @return
     */
    public static String apiTkbHocky(String user, String pass){
        return host + "user=" + user
                + "&pass=" + pass
                + "&act=tkb&option=lhk";
    }

    public static String apiTkb(String user, String pass, String id){
        return host + "user=" + user
                + "&pass=" + pass
                + "&act=tkb&option=ln&id=" + id;
    }

    public static String apiHostMail(String user, String pass){
        return "http://trautre.azurewebsites.net/api.php?" + "user=" + user
                + "&pass=" + pass
                + "&act=mail";
    }


    public static String apiHdptHocky(String user, String pass){
        return host
                + "user=" + user
                + "&pass=" + pass
                + "&act=hdpt&option=lhk";
    }

    public static String apiHdpt(String user, String pass, String id){
        return host
                + "user=" + user
                + "&pass=" + pass
                + "&id=" + id
                + "&act=hdpt";
    }
}
