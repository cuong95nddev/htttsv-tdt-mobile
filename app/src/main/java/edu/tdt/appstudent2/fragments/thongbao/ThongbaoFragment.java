package edu.tdt.appstudent2.fragments.thongbao;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brandongogetap.stickyheaders.StickyLayoutManager;
import com.github.ybq.endless.Endless;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.Token;
import edu.tdt.appstudent2.actitities.thongbao.ThongbaoWebviewActivity;
import edu.tdt.appstudent2.adapters.thongbao.ThongbaoRecyclerViewAdapter;
import edu.tdt.appstudent2.api.Api;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.thongbao.ThongbaoItem;
import edu.tdt.appstudent2.utils.StringUtil;
import edu.tdt.appstudent2.utils.Tag;
import edu.tdt.appstudent2.views.custom.TopSnappedStickyLayoutManager;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ThongbaoFragment extends Fragment {
    private View inflatedView = null;
    private String idDonVi;
    private RecyclerView recyclerView;
    private ThongbaoRecyclerViewAdapter adapter;
    private StickyLayoutManager manager;

    private int pageNow;
    private int pageTotal;
    private Realm realm;
    private User user;
    private String userText, passText;

    private boolean isLoaded;

    private SwipeRefreshLayout swipeContainer;
    private Endless endless;

    private void khoiTao(){
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            idDonVi = bundle.getString(Tag.idDonVi);
        }
        pageNow = 1;
        pageTotal = -1;
        isLoaded = false;
    }
    private void anhXa(){
        khoiTao();
        recyclerView = (RecyclerView) inflatedView.findViewById(R.id.recyclerview);
        adapter = new ThongbaoRecyclerViewAdapter(inflatedView.getContext());
        manager = new TopSnappedStickyLayoutManager(inflatedView.getContext(), adapter);
        manager.elevateHeaders(1);

        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);

        View loadingView = View.inflate(inflatedView.getContext(), R.layout.recyclerview_loading, null);
        loadingView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        endless = Endless.applyTo(recyclerView, loadingView);
        endless.setAdapter(adapter);

        endless.setLoadMoreListener(new Endless.LoadMoreListener() {
            @Override
            public void onLoadMore(int page) {
                if(pageTotal > pageNow || pageTotal == -1) {
                    pageNow++;
                    readThongBao();
                }else{
                    endless.setLoadMoreAvailable(false);
                }
            }
        });

        swipeContainer = (SwipeRefreshLayout) inflatedView.findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(R.color.colorAccent);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isLoaded = false;
                readThongBao();
            }
        });
    }
    public ThongbaoFragment() {

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
        this.inflatedView = inflater.inflate(R.layout.fragment_thongbao, container, false);
        anhXa();

        showThongBaoOffline();

        adapter.setOnItemClickListener(new ThongbaoRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                try {
                    ThongbaoItem thongbaoItem = adapter.getItem(position);
                    Intent thongBaoWebviewIntent = new Intent(getContext(), ThongbaoWebviewActivity.class);
                    thongBaoWebviewIntent.putExtra(Tag.idThongBao, thongbaoItem.getId().replace("-" + idDonVi, ""));
                    startActivity(thongBaoWebviewIntent);
                    if(thongbaoItem.isNew()){
                        unNewItem(thongbaoItem);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        return inflatedView;
    }

    private void unNewItem(final ThongbaoItem thongbaoItem){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                thongbaoItem.setNew(false);
                adapter.notifyDataSetChanged();
                realm.copyToRealmOrUpdate(thongbaoItem);
            }
        });
    }

    private void showThongBaoOffline(){
        RealmResults<ThongbaoItem> realmResults = realm.where(ThongbaoItem.class)
                .equalTo("idDonVi", idDonVi)
                .findAllSorted("id", Sort.DESCENDING);
        for(ThongbaoItem thongbaoItem : realmResults){
            adapter.addItem(thongbaoItem);
        }
        readThongBao();
    }

    private void readThongBao(){
        if(!isLoaded){
            swipeContainer.setRefreshing(true);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new readThongBao().execute("");
            }
        });
    }

    public class readThongBao extends AsyncTask<String, Integer, ArrayList<ThongbaoItem>> {

        @Override
        protected ArrayList<ThongbaoItem> doInBackground(String... strings) {
            ArrayList<ThongbaoItem> thongbaoItems = new ArrayList<ThongbaoItem>();
            try {
                Document doc = Jsoup.connect(Api.host)
                        .data("user", userText)
                        .data("token", Token.getToken(userText, passText))
                        .data("act", "tb")
                        .data("lv", idDonVi)
                        .data("page", Integer.toString(pageNow))
                        .timeout(30000)
                        .get();
                JSONObject root = new JSONObject(doc.text());

                if(!root.getBoolean("status")){
                    return thongbaoItems;
                }

                JSONObject dataObject = root.getJSONObject("data");
                if(dataObject != null){
                    JSONArray thongBaoArray = dataObject.getJSONArray("thongbao");
                    ThongbaoItem thongBaoNew = null;
                    for(int i = 0; i < thongBaoArray.length(); i++){
                        JSONObject thongBaoItem = thongBaoArray.getJSONObject(i);
                        thongBaoNew = new ThongbaoItem();
                        thongBaoNew.setTitle(StringUtil.thongBaoFormat(thongBaoItem.getString("title")));
                        //Khóa chính là id + "-" + idDonvi nên idDonvi phải được ghi trước
                        thongBaoNew.setIdDonVi(idDonVi);
                        thongBaoNew.setId(thongBaoItem.getString("id"));
                        thongBaoNew.setDate(StringUtil.thongBaoGetDate(thongBaoItem.getString("title")));
                        thongBaoNew.setNew(thongBaoItem.getBoolean("unread"));
                        thongbaoItems.add(thongBaoNew);
                    }
                    pageTotal = Integer.parseInt(dataObject.getString("numpage"));
                }
            } catch (IOException e) {
                //
            } catch (JSONException e) {
                e.printStackTrace();
            }catch (java.lang.IllegalStateException e) {
                realm.close();
            }
            return thongbaoItems;
        }

        @Override
        protected void onPostExecute(ArrayList<ThongbaoItem> thongbaoItems) {
            super.onPostExecute(thongbaoItems);
            endless.loadMoreComplete();
            if(thongbaoItems.size() > 0){
                if(!isLoaded){
                    isLoaded = true;
                    adapter.clear();
                    swipeContainer.setRefreshing(false);
                }
                for (ThongbaoItem e: thongbaoItems){
                    adapter.addItem(e);
                    try {
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(e);
                        realm.commitTransaction();
                    }catch (java.lang.IllegalStateException a){
                        realm.close();
                    }
                }
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
