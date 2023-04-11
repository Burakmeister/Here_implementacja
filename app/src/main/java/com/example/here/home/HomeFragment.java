package com.example.here.home;

import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.here.R;
import com.example.here.constants.ActivityType;
import com.here.sdk.core.Color;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoPolyline;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapview.MapMeasure;
import com.here.sdk.mapview.MapPolyline;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;
import com.here.sdk.routing.PedestrianOptions;
import com.here.sdk.routing.CalculateRouteCallback;
import com.here.sdk.routing.Route;
import com.here.sdk.routing.RoutingEngine;
import com.here.sdk.routing.RoutingError;
import com.here.sdk.routing.Waypoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Widok strony głównej

public class HomeFragment extends Fragment {

    private FriendStatusAdapter statusAdapter;
    private View view;
    private RecyclerView statusView;

    String[] items = {"Marsz","Bieganie","Jazda na rowerze","Kajakarstwo"};
    AutoCompleteTextView autoCompleteTxt;
    ArrayAdapter<String> adapterItems;

    private double lastTourStartV1 = 53.41178404163292, lastTourStartV2 = 23.516119474276664,           // pobierane z bazy danych / z pamieci urzadzenia
            lastTourEndV1 = 53.1276662351446, lastTourEndV2 = 23.160716949523863;

    public HomeFragment() {
        // require a empty public constructor
    }

//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.fragment_home, container, false);;
////        this.mapView = view.findViewById(R.id.mapView);
////        this.mapView.onCreate(savedInstanceState);
//
////        setToLastRoute(new Waypoint(new GeoCoordinates(lastTourStartV1, lastTourStartV2)), new Waypoint(new GeoCoordinates(lastTourEndV1, lastTourEndV2)));     //rysowanie poprzedniej trasy po wspolrzednych*/
//
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//
//        chooseDiscipline();
//        return view;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);;
        this.statusView = view.findViewById(R.id.recyclerView_OnlineFriends);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        this.updateView();
        chooseDiscipline();

        return view;
    }

    private void updateView(){
        FriendStatus example = new FriendStatus("brak", "Aga", ActivityType.CYCLING, 69, "Aga123");
        List<FriendStatus> friends =  new ArrayList<>();
        friends.add(example);

        if(statusAdapter == null){
            statusAdapter = new FriendStatusAdapter(friends);
            statusView.setAdapter(statusAdapter);
        }else{
            statusAdapter.notifyDataSetChanged();
        }
    }

    public void chooseDiscipline(){
        autoCompleteTxt = view.findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<String>(getActivity(),R.layout.list_item,items);
        autoCompleteTxt.setText(items[0]);
        autoCompleteTxt.setAdapter(adapterItems);
        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                String item = parent.getItemAtPosition(position).toString();
//                Toast.makeText(getActivity().getApplicationContext(),"Item: "+item,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        this.updateView();
    }

    private class FriendStatusHolder extends RecyclerView.ViewHolder{
        private FriendStatus status;
        private TextView nickname;
        private final TextView text;
        private final ImageView icon;
        public FriendStatusHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.status_list_item, parent, false));

            this.text = itemView.findViewById(R.id.status_item_text);
            this.icon = itemView.findViewById(R.id.status_item_image);
            this.nickname = itemView.findViewById(R.id.status_item_nickname);
        }

        public void bind(FriendStatus status){
            this.status = status;
            text.setText(status.getInfo());
            nickname.setText(status.getNickname());
            icon.setImageBitmap(BitmapFactory.decodeFile(this.status.getImageSource()));
        }
    }

    private class FriendStatusAdapter extends RecyclerView.Adapter<FriendStatusHolder>{
        private final List<FriendStatus> status;
        public FriendStatusAdapter(List<FriendStatus> status){
            this.status = status;
        }
        @NonNull
        @Override
        public FriendStatusHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new FriendStatusHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull FriendStatusHolder holder, int position) {
            FriendStatus friendStatus = this.status.get(position);
            holder.bind(friendStatus);
        }

        @Override
        public int getItemCount() {
            return status.size();
        }
    }
}
