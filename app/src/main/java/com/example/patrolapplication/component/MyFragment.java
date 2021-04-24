package com.example.patrolapplication.component;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.example.patrolapplication.R;
import com.example.patrolapplication.network.DataController;
import com.example.patrolapplication.network.dao.UserDao;
import com.example.patrolapplication.pojo.AbnormalRecord;
import com.example.patrolapplication.utils.LocalDataController;
import com.example.patrolapplication.utils.MyData;
import com.example.patrolapplication.utils.SyncData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyFragment extends Fragment {
    private static final String TAG = "MyFragment";

    private RecyclerView recyclerView;
    private int position;
    private Map<String, Object> map;
    private UserDao userDao;

    public static Map<String, String> locationMap;

    public MyFragment() {}

    public MyFragment(int pos) {
        this.position = pos;
        String userId = LocalDataController.getUserId();
        map = new HashMap<>();
        if(pos == 0) // TripRecord: patrol
            map.put("patrol", userId);
        else{
            map.put("userId", userId);
        }
        userDao = new UserDao() {
            @Override
            public void userOperate(Map<String, Object> map) {
                if(recyclerView != null){
                    List<Map<String, Object>> res = new Gson().fromJson((String)map.get("data"),
                            new TypeToken<List<Map<String, Object>>>(){}.getType());
                    switch (pos){
                        case 0:
                            recyclerView.setAdapter(new TripAdapter(res));
                            break;
                        case 1:
                            recyclerView.setAdapter(new PatrolAdapter(res));
                            break;
                        case 2:
                            recyclerView.setAdapter(new AbnormalAdapter(res));
                        default:
                            break;
                    }
                }
            }

            @Override
            public void timeout() {
                MyData.timeoutToast(getContext());
            }
        };
        if(locationMap == null){
            DataController.queryLocationList(new UserDao() {
                @Override
                public void userOperate(Map<String, Object> map) {
                    List<Map<String, Object>> res = new Gson().fromJson((String)map.get("data"),
                            new TypeToken<List<Map<String, Object>>>(){}.getType());
                    locationMap = new HashMap<>();
                    for (Map<String, Object> temp : res) {
                        locationMap.put((String)temp.get("id"), (String)temp.get("name"));
                    }
                    locationMap.put("00000000", "其他");
                }

                @Override
                public void timeout() {
                    MyData.timeoutToast(getContext());
                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: " + position);
        if(map != null && userDao != null){
            switch (position){
                case 0:
                    DataController.queryTripRecord(map, userDao);
                    break;
                case 1:
                    DataController.queryPatrolRecord(map, userDao);
                    break;
                case 2:
                    DataController.queryAbnormalRecord(map, userDao);
                    break;
                default:
                    break;
            }
        }else{
            Toast.makeText(getContext(), "map and userdao is null", Toast.LENGTH_SHORT).show();
        }
        return inflater.inflate(R.layout.my_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated " + position);
        recyclerView = getView().findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
    }

    private static class MyHolder extends RecyclerView.ViewHolder{
        public MyHolder(View itemView){
            super(itemView);
        }
    }

    private static class TripAdapter extends RecyclerView.Adapter<MyHolder>{
        private List<Map<String,Object>> mapList;
        TripAdapter(List<Map<String,Object>> mapList){
            this.mapList = mapList;
        }
        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.adapter_triprecord, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            ((EditText)holder.itemView.findViewById(R.id.start_time)).setText(
                    SyncData.str2Str((String)mapList.get(position).get("startTime")));
            ((EditText)holder.itemView.findViewById(R.id.end_time)).setText(
                    SyncData.str2Str((String)mapList.get(position).get("endTime")));
            ((EditText)holder.itemView.findViewById(R.id.total_loc_num)).setText(
                    "" + Math.round((double)mapList.get(position).get("totalLocNum")));
            ((EditText)holder.itemView.findViewById(R.id.patrol_num)).setText(
                    "" + Math.round((double)mapList.get(position).get("patrolNum")));
            ((EditText)holder.itemView.findViewById(R.id.abnormal_num)).setText(
                    "" + Math.round((double)mapList.get(position).get("abnormalNum")));
        }
        @Override
        public int getItemCount() {
            return mapList.size();
        }
    }

    private static class PatrolAdapter extends RecyclerView.Adapter<MyHolder>{
        private List<Map<String, Object>> mapList;

        public PatrolAdapter(List<Map<String, Object>> mapList) {
            this.mapList = mapList;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.adapter_patrolrecord, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            // 将id转化为地点名称
            String location = (String)mapList.get(position).get("patrolLocId");
            if(locationMap != null){
                location = locationMap.get(location);
            }
            ((EditText)holder.itemView.findViewById(R.id.patrolRecordLocationEdit)).setText(location);
            ((EditText)holder.itemView.findViewById(R.id.patrolRecordTimeEdit)).setText(
                    SyncData.str2Str((String)mapList.get(position).get("time")));
            String condition = "异常";
            if(1 - (double)mapList.get(position).get("condition") < 0.1){
                condition = "正常";
            }
            ((EditText)holder.itemView.findViewById(R.id.patrolRecordConditionEdit)).setText(condition);
        }

        @Override
        public int getItemCount() {
            return mapList.size();
        }
    }

    private static class AbnormalAdapter extends RecyclerView.Adapter<MyHolder>{
        private List<Map<String, Object>> mapList;

        public AbnormalAdapter(List<Map<String, Object>> mapList) {
            this.mapList = mapList;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.adapter_abnormalrecord, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            // 将id转化为地点名称
            String location = (String)mapList.get(position).get("patrolLocId");
            if(locationMap != null){
                location = locationMap.get(location);
            }
            ((EditText)holder.itemView.findViewById(R.id.abnormalLocationEdit)).setText(location);
            ((EditText)holder.itemView.findViewById(R.id.abnormalTimeEdit)).setText(
                    SyncData.str2Str((String)mapList.get(position).get("time")));
            ((EditText)holder.itemView.findViewById(R.id.abnormalItemEdit)).setText(
                    (String)mapList.get(position).get("abnormalItem"));
            ((EditText)holder.itemView.findViewById(R.id.abnormalDetailEdit)).setText(
                    (String)mapList.get(position).get("abnormalDetail"));
            // 转为图片格式
            RequestOptions options = new RequestOptions();

            String pictureLink = (String)mapList.get(position).get("pictureLink");
            String url = SyncData.ipAddress + "image/" + pictureLink.substring(37, pictureLink.length());
            Glide.with(holder.itemView).load(url).apply(options)
                    .fitCenter()
                    .into((ImageView)holder.itemView.findViewById(R.id.abnormalImage));
        }

        @Override
        public int getItemCount() {
            return mapList.size();
        }
    }
}