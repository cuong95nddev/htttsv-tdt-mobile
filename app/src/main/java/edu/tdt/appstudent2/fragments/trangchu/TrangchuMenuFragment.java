package edu.tdt.appstudent2.fragments.trangchu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.actitities.cnsv.CnsvActivity;
import edu.tdt.appstudent2.actitities.diem.DiemActivity;
import edu.tdt.appstudent2.actitities.email.EmailActivity;
import edu.tdt.appstudent2.actitities.email.EmailNewActivity;
import edu.tdt.appstudent2.actitities.hdpt.HdptActivity;
import edu.tdt.appstudent2.actitities.hocphi.HocphiActivity;
import edu.tdt.appstudent2.actitities.lichthi.LichThiActivity;
import edu.tdt.appstudent2.actitities.ndtt.NdttActivity;
import edu.tdt.appstudent2.actitities.sakai.SakaiActivity;
import edu.tdt.appstudent2.actitities.thongbao.ThongbaoActivity;
import edu.tdt.appstudent2.actitities.tkb.TkbActivity;
import edu.tdt.appstudent2.adapters.trangchu.TrangchuMenuRecyclerViewAdapter;
import edu.tdt.appstudent2.models.trangchu.TrangchuMenuItem;
import edu.tdt.appstudent2.utils.Tag;


public class TrangchuMenuFragment extends Fragment{
    private View inflatedView = null;
    private RecyclerView recyclerView;
    private TrangchuMenuRecyclerViewAdapter adapter;
    private StaggeredGridLayoutManager manager;
    private ArrayList<TrangchuMenuItem> lists;
    private int column;
    public void khoiTao(){
        lists = new ArrayList<TrangchuMenuItem>();
    }
    public void anhXa(){
        khoiTao();

        if(getActivity().getResources().getConfiguration().orientation == 2){
            column = 3;
        }else{
            column = 3;
        }

        recyclerView = (RecyclerView) inflatedView.findViewById(R.id.recyclerview);
        adapter = new TrangchuMenuRecyclerViewAdapter(inflatedView.getContext(), lists);
        manager = new StaggeredGridLayoutManager(column, StaggeredGridLayoutManager.VERTICAL);
        manager.setSpanCount(column);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
    }
    public TrangchuMenuFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflatedView =  inflater.inflate(R.layout.fragment_trangchu_menu, container, false);
        anhXa();
        addMenu();
        adapter.setOnItemClickListener(new TrangchuMenuRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (lists.get(position).getTag()){
                    case Tag.TAG_MENU_THONGBAO:
                        Intent thongBao = new Intent(getActivity(), ThongbaoActivity.class);
                        startActivity(thongBao);
                        break;
                    case Tag.TAG_MENU_DIEM:
                        Intent diem = new Intent(getActivity(), DiemActivity.class);
                        startActivity(diem);
                        break;
                    case Tag.TAG_MENU_HOCPHI:
                        Intent hocPhi = new Intent(getActivity(), HocphiActivity.class);
                        startActivity(hocPhi);
                        break;
                    case Tag.TAG_MENU_TKB:
                        Intent tkb = new Intent(getActivity(), TkbActivity.class);
                        startActivity(tkb);
                        break;
                    case Tag.TAG_MENU_EMAIL:
                        Intent email = new Intent(getActivity(), EmailActivity.class);
                        startActivity(email);
                        break;
                    case Tag.TAG_MENU_HDPT:
                        Intent hdpt = new Intent(getActivity(), HdptActivity.class);
                        startActivity(hdpt);
                        break;
                    case Tag.TAG_MENU_LICHTHI:
                        Intent lichThi = new Intent(getActivity(), LichThiActivity.class);
                        startActivity(lichThi);
                        break;
                    case Tag.TAG_MENU_NDTT:
                        Intent ndtt = new Intent(getActivity(), NdttActivity.class);
                        startActivity(ndtt);
                        break;
                    case Tag.TAG_MENU_CNSV:
                        Intent cnsv = new Intent(getActivity(), CnsvActivity.class);
                        startActivity(cnsv);
                        break;
                    case Tag.TAG_MENU_SAKAI:
                        Intent sakai = new Intent(getActivity(), SakaiActivity.class);
                        startActivity(sakai);
                        break;
                    case Tag.TAG_MENU_BUG:
                        Intent emailBug = new Intent(getActivity(), EmailNewActivity.class);
                        emailBug.putExtra(EmailNewActivity.EXTRA_BUG, true);
                        emailBug.putExtra(EmailNewActivity.EXTRA_TO, "51403238@student.tdt.edu.vn");
                        emailBug.putExtra(EmailNewActivity.EXTRA_SUBJECT, "Báo lỗi");
                        startActivity(emailBug);
                        break;
                }
            }
        });

        return inflatedView;
    }

    private void addMenu(){
        lists.add(new TrangchuMenuItem(Tag.TAG_MENU_THONGBAO,"THÔNG BÁO","Nhận thông báo mới từ Đoàn Trường", R.drawable.icon_menu_0, "#689F39"));
        lists.add(new TrangchuMenuItem(Tag.TAG_MENU_EMAIL,"EMAIL","Email sinh viên", R.drawable.icon_menu_1, "#8CC34B"));

        lists.add(new TrangchuMenuItem(Tag.TAG_MENU_TKB,"TKB","Thông tin lịch học các học kỳ", R.drawable.icon_menu_2, "#F44337"));
        lists.add(new TrangchuMenuItem(Tag.TAG_MENU_LICHTHI,"LỊCH THI","Thông tin lịch thi các học kỳ", R.drawable.icon_menu_3, "#FF9801"));

        lists.add(new TrangchuMenuItem(Tag.TAG_MENU_DIEM,"ĐIỂM","Tổng hợp kết quả học tập", R.drawable.icon_menu_4, "#03A9F4"));
        lists.add(new TrangchuMenuItem(Tag.TAG_MENU_HDPT,"HOẠT ĐỘNG","Điểm dánh giá các hoạt động phong trào", R.drawable.icon_menu_5, "#3F51B5"));

        lists.add(new TrangchuMenuItem(Tag.TAG_MENU_HOCPHI,"HỌC PHÍ","Thông tin học phí học kỳ", R.drawable.icon_menu_7, "#9C37CB"));
        //lists.add(new TrangchuMenuItem(Tag.TAG_MENU_THONGTIN,"THÔNG TIN SV","Thông tin hồ sơ Sinh viên của bạn", R.drawable.icon_menu_6, "#AA2E85"));

        lists.add(new TrangchuMenuItem(Tag.TAG_MENU_SAKAI,"SAKAI","Thông tin học phí học kỳ", R.drawable.icon_menu_11, "#9C37CB"));


        lists.add(new TrangchuMenuItem(Tag.TAG_MENU_CNSV,"CNSV","Thông tin học phí học kỳ", R.drawable.icon_menu_8, "#9C37CB"));
        lists.add(new TrangchuMenuItem(Tag.TAG_MENU_NDTT,"NĐTT","Thông tin học phí học kỳ", R.drawable.icon_menu_9, "#9C37CB"));

        lists.add(new TrangchuMenuItem(Tag.TAG_MENU_BUG,"BÁO LỖI","Thông tin học phí học kỳ", R.drawable.icon_menu_12, "#9C37CB"));

        adapter.notifyDataSetChanged();
    }

}
