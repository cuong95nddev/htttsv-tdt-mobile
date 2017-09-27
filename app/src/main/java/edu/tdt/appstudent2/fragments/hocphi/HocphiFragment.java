package edu.tdt.appstudent2.fragments.hocphi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kennyc.view.MultiStateView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.Token;
import edu.tdt.appstudent2.adapters.hocphi.HocphiRecyclerViewAdapter;
import edu.tdt.appstudent2.api.Api;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.hocphi.HocphiChitiet;
import edu.tdt.appstudent2.models.hocphi.HocphiHeaderItem;
import edu.tdt.appstudent2.models.hocphi.HocphiItem;
import edu.tdt.appstudent2.models.hocphi.HocphiMucItem;
import edu.tdt.appstudent2.models.hocphi.HocphiThanhtoanItem;
import edu.tdt.appstudent2.models.hocphi.HocphiTitleItem;
import edu.tdt.appstudent2.utils.Tag;
import io.realm.Realm;
import io.realm.RealmList;


public class HocphiFragment extends Fragment {
    private View inflatedView = null;
    private String idHocKy;
    private RecyclerView recyclerView;
    private HocphiRecyclerViewAdapter adapter;
    private StaggeredGridLayoutManager manager;
    private ArrayList<Object> list;

    private Realm realm;
    private User user;
    private String userText, passText;
    private HocphiItem hocphiItem;
    private SwipeRefreshLayout swipeContainer;
    private MultiStateView mMultiStateView;


    private void khoiTao(){
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            idHocKy = bundle.getString(Tag.idHocKy);
        }
        list = new ArrayList<Object>();
    }
    private void anhXa(){
        khoiTao();
        recyclerView = (RecyclerView) inflatedView.findViewById(R.id.recyclerview);
        adapter = new HocphiRecyclerViewAdapter(list);
        manager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        manager.setSpanCount(1);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        swipeContainer = (SwipeRefreshLayout) inflatedView.findViewById(R.id.swipeContainer);

        mMultiStateView = (MultiStateView) inflatedView.findViewById(R.id.multiStateView);
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();
            }
        });
    }
    public HocphiFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();
        userText = user.getUserName();
        passText = user.getPassWord();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView =  inflater.inflate(R.layout.fragment_hocphi, container, false);
        anhXa();

        hocphiItem = realm.where(HocphiItem.class)
                .equalTo("idHocKy", idHocKy)
                .findFirst();
        if(hocphiItem == null){
            getOnline();
        }else {
            showOffline();
        }

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(false);
                reload();
            }
        });
        return inflatedView;
    }

    private void reload(){
        list.clear();
        adapter.notifyDataSetChanged();
        getOnline();
    }

    private void showOffline(){
        showThongTin();
    }

    private void getOnline(){
        getHocPhi();
    }

    private void showThongTin(){
        if(hocphiItem != null) {
            try{
                if(Integer.parseInt(hocphiItem.getHocPhiPhaiNop().replaceAll(",", "")) == 0){
                    throw new NumberFormatException();
                }
            }catch (NumberFormatException e){
                mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
                return;
            }
            list.add(new HocphiHeaderItem(hocphiItem.getHocPhiPhaiNop(), hocphiItem.getNgayCapNhap()));
            RealmList<HocphiChitiet> hocphiChitiets = hocphiItem.getHocphiChitiets();

            if(hocphiItem.getHocphiThanhtoanItems().size() > 0)
                list.add(new HocphiTitleItem("THANH TOÁN"));

            for(HocphiThanhtoanItem hocphiThanhtoanItem : hocphiItem.getHocphiThanhtoanItems()){
                list.add(hocphiThanhtoanItem);
            }

            list.add(new HocphiTitleItem("THÔNG TIN"));
            list.add(new HocphiMucItem("Nợ kỳ trước", hocphiItem.getNoHocKyTruoc()));
            list.add(new HocphiMucItem("Học phí học kỳ", hocphiItem.getHocPhiHocKy()));
            list.add(new HocphiMucItem("Miễn giảm", hocphiItem.getMienGiam()));
            list.add(new HocphiMucItem("Học phí phải nộp", hocphiItem.getHocPhiPhaiNop()));
            list.add(new HocphiMucItem("Học phí đã nộp", hocphiItem.getHocPhiDaNop()));
            list.add(new HocphiMucItem("Số tiền còn phải nộp", hocphiItem.getHocPhiConPhaiNop()));

            if(hocphiChitiets.size() > 0)
                list.add(new HocphiTitleItem("CHI TIẾT"));
            for(int i = 0 ; i < hocphiChitiets.size(); i++){
                list.add(hocphiChitiets.get(i));
            }


            adapter.notifyDataSetChanged();
        }else {

        }
    }

    private void getHocPhi(){
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new getHocPhi().execute("");
            }
        });
    }
    public class getHocPhi extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {

            try {
                Document doc = Jsoup.connect(Api.host)
                        .data("user", userText)
                        .data("token", Token.getToken(userText, passText))
                        .data("act", "hp")
                        .data("option", "lhp")
                        .data("id", idHocKy)
                        .timeout(30000)
                        .get();
                return doc.text();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            if(s != null){
                try {
                    JSONObject root = new JSONObject(s);
                    if(root.has("status")) {
                        if (root.getBoolean("status")) {
                            realm.beginTransaction();
                            JSONObject data = root.getJSONObject("data");
                            JSONArray hpArray = data.getJSONArray("hp");
                            if(hpArray.length() > 0) {
                                hocphiItem = new HocphiItem();
                                JSONObject hp = hpArray.getJSONObject(0);
                                hocphiItem.setId(Integer.toString(hp.getInt("ID")));
                                hocphiItem.setIdHocKy(idHocKy);
                                hocphiItem.setHocPhiConPhaiNop(hp.getString("StrRemain"));
                                hocphiItem.setHocPhiDaNop(hp.getString("StrPaid"));
                                hocphiItem.setHocPhiHocKy(hp.getString("StrTotal"));
                                hocphiItem.setHocPhiPhaiNop(hp.getString("StrSubTotal"));
                                hocphiItem.setMienGiam(hp.getString("StrDecrease"));
                                hocphiItem.setNoHocKyTruoc(hp.getString("StrDebtBefore"));
                                hocphiItem.setNgayCapNhap(hp.getString("StrThoiGianCapNhatSauCung"));

                                JSONArray chitiethp = data.getJSONArray("chitiethp");
                                JSONObject chitiethpObject = null;
                                HocphiChitiet hocphiChitiet = null;

                                for (int i = 0; i < chitiethp.length(); i++) {
                                    chitiethpObject = chitiethp.getJSONObject(i);
                                    hocphiChitiet = new HocphiChitiet();
                                    hocphiChitiet.setMaMonHoc(chitiethpObject.getString("SubjectID"));
                                    hocphiChitiet.setSoTien(chitiethpObject.getString("StrThanhTien"));
                                    hocphiChitiet.setTenMonHoc(chitiethpObject.getString("TenMH"));
                                    hocphiItem.getHocphiChitiets().add(hocphiChitiet);
                                }

                                JSONArray chitietttArray = data.getJSONArray("chitiettt");
                                for(int i = 0 ; i < chitietttArray.length(); i++){
                                    JSONObject chitiettt = chitietttArray.getJSONObject(i);
                                    HocphiThanhtoanItem hocphiThanhtoanItem = new HocphiThanhtoanItem();
                                    hocphiThanhtoanItem.setHinhThucThanhToan(chitiettt.getString("HinhThucThanhToanID"));
                                    hocphiThanhtoanItem.setNgayThanhToan(chitiettt.getString("StrPaidDate"));
                                    hocphiThanhtoanItem.setSoTienThanhToan(chitiettt.getString("StrTotalCost"));
                                    hocphiItem.getHocphiThanhtoanItems().add(hocphiThanhtoanItem);
                                }
                                realm.copyToRealmOrUpdate(hocphiItem);
                            }
                            realm.commitTransaction();
                        } else {
                            // empty
                            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
                        }
                    }else{
                        // error
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (java.lang.IllegalStateException e){
                    realm.close();
                }
                showThongTin();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
