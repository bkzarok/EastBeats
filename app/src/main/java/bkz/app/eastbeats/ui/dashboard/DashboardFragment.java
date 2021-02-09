package bkz.app.eastbeats.ui.dashboard;

import android.graphics.ColorSpace;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import bkz.app.eastbeats.MainActivity;
import bkz.app.eastbeats.Models.MediaObject;
import bkz.app.eastbeats.Models.Post;
import bkz.app.eastbeats.R;
import bkz.app.eastbeats.Services.FireBase_Services;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    private VideoPlayerRecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private FireBase_Services services;
    private FirebaseStorage storage;
    private ArrayList<MediaObject> mediaObjects;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        recyclerView = root.findViewById(R.id.recycler_view);
        services = new FireBase_Services();
        storage = FirebaseStorage.getInstance();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(itemDecoration);
        mediaObjects = new ArrayList<>();
        //ArrayList<MediaObject> mediaObjects = services.getPost();
        //recyclerView.setMediaObjects(mediaObjects);
        //VideoPlayerRecyclerAdapter adapter = new VideoPlayerRecyclerAdapter(mediaObjects, initGlide());
        //recyclerView.setAdapter(adapter);
         fetch();
       // initRecyclerView();




        return root;
    }


//    public class ViewHolder extends RecyclerView.ViewHolder {
//        FrameLayout media_container;
//        TextView title;
//        ImageView thumbnail, volumeControl;
//        ProgressBar progressBar;
//        View parent;
//        RequestManager requestManager;
//
//        public ViewHolder(View itemView)
//        {
//            super(itemView);
//            parent = itemView;
//            media_container = itemView.findViewById(R.id.media_container);
//            thumbnail = itemView.findViewById(R.id.thumbnail);
//            title = itemView.findViewById(R.id.title);
//            progressBar = itemView.findViewById(R.id.progressBar);
//            volumeControl = itemView.findViewById(R.id.volume_control);
//        }
//
//
//        public void onBind(MediaObject mediaObject) {
//            long interval = getPosition()*1000;
//           RequestOptions options = new RequestOptions().frame(interval);
//            parent.setTag(this);
//            title.setText(mediaObject.getTitle());
//            Glide.with(getContext()).asBitmap().load(mediaObject.getMedia_url()).apply(options).into(thumbnail);
//        }
//
////        public void setVideo_post(Uri uri) {
////           // Log.d("Vidoe uir", uri.toString()) ;
////            long interval = getPosition()*1000;
////            RequestOptions options = new RequestOptions().frame(interval);
////
////            Glide.with(getContext()).asBitmap().load(uri).apply(options).into(video_thumbnail);
////            videoView.setVideoURI(uri);
////            MediaController mediaController = new MediaController(getContext());
////            videoView.setMediaController(mediaController);
////        }
//    }
    @Override
    public void onDestroy() {
        if(recyclerView!=null)
            recyclerView.releasePlayer();
        super.onDestroy();
    }



    private void fetch( ) {

        adapter = new FirebaseRecyclerAdapter<MediaObject,VideoPlayerViewHolder>(services.fetchPost()) {
            @Override
            public VideoPlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.post_item, parent, false);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = recyclerView.getChildLayoutPosition(view);
                        Toast.makeText(getContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                    }
                });
                return new VideoPlayerViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(VideoPlayerViewHolder holder, final int position, MediaObject model) {
               mediaObjects.add(model);
               recyclerView.setMediaObjects(mediaObjects);
               holder.onBind(model, initGlide());
            }
        };
        recyclerView.setMediaObjects(mediaObjects);
        recyclerView.setAdapter(adapter);
    }

    private void initRecyclerView(ArrayList<MediaObject> mediaObjects){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setMediaObjects(mediaObjects);
        VideoPlayerRecyclerAdapter adapter = new VideoPlayerRecyclerAdapter(mediaObjects, initGlide());
        recyclerView.setAdapter(adapter);
    }

    private RequestManager initGlide(){
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}