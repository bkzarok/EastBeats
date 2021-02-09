package bkz.app.eastbeats.Services;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import bkz.app.eastbeats.Models.MediaObject;
import bkz.app.eastbeats.Models.Post;

public class FireBase_Services
{
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseStorage storage;

    public FireBase_Services()
    {
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

    }


    public void newPost(String author, String videoUrl, String caption, int trend) {


        Uri file = Uri.fromFile(new File(videoUrl));


        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        StorageReference videoref = storageRef.child("videos/"+UUID.randomUUID().toString() + new Date().toString()+file.getLastPathSegment());
        UploadTask uploadTask = videoref.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...

                Log.d("Video Upload Complete", "Video Upload OnSuccess");

                Task<Uri> str = videoref.getDownloadUrl();
                str.addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        myRef = database.getReference("posts");
                        // String key = myRef.child("posts").push().getKey();

                        Post   post = new Post("kz", "empty caption",task.getResult().toString(), 0.0);
                        Map<String, Object> postValues = post.toMap();
                        myRef.push().setValue(postValues);

                        File  f = new File(videoUrl);
                        f.delete();

                    }
                });
            }
        });


        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously

    }

    public Uri tempuri;
    public Uri getFileFromStorage(String url)
    {
        StorageReference httpsReference = storage.getReferenceFromUrl(url);


       httpsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Log.d("ON Sucess", uri.toString());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d("ON Sucess", "Fail to get uri");

            }
        });

       //Log.d("uir in Service", tempuri.toString());
       return tempuri;
    }

    public ArrayList<MediaObject> getPost()
    {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("posts");
        ArrayList<MediaObject> mediaObject = new ArrayList<>();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("onDataha ---", ""+snapshot.getValue());
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    // TODO: handle the post

                    mediaObject.add( new MediaObject(postSnapshot.child("author").getValue().toString(),
                            postSnapshot.child("caption").getValue().toString(),
                            postSnapshot.child("videoUrl").getValue().toString(),0.0));


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Log.d("")
        for (MediaObject n :mediaObject
             ) {
               Log.d("URI Is********* ",""+ n.getMedia_url());
        }

        return mediaObject;
    }
    public FirebaseRecyclerOptions<MediaObject> fetchPost()
    {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("posts");

        FirebaseRecyclerOptions<MediaObject> options =
                new FirebaseRecyclerOptions.Builder<MediaObject>()
                        .setQuery(query, new SnapshotParser<MediaObject>() {
                            @NonNull
                            @Override
                            public MediaObject parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new MediaObject(snapshot.child("author").getValue().toString(),
                                        snapshot.child("caption").getValue().toString(),
                                        snapshot.child("videoUrl").getValue().toString(),0.0);

                            }
                        })
                        .build();

        Log.d("fetchPost options", "size"+options.getSnapshots().size());
        return options;
    }

}
