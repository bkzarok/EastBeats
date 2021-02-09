package bkz.app.eastbeats.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.io.IOException;

import bkz.app.eastbeats.Services.FireBase_Services;
import bkz.app.eastbeats.Models.Post;
import bkz.app.eastbeats.R;

public class HomeFragment extends Fragment
{

    private static final int PICKFILE_REQUEST_CODE = 1 ;
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 1;
    private HomeViewModel homeViewModel;
    private Button sendButton;
    private Button uploadButton;
    private FireBase_Services services;
    private   String videoPath;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        services = new FireBase_Services();
        final TextView textView = root.findViewById(R.id.isUploadTextView);
        uploadButton = root.findViewById(R.id.uploadButton);
        sendButton = root.findViewById(R.id.sendButton);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    } else {


                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},23
                        );
                    }
                }
                showVideoChooserDialog();

            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    uploadVideo(videoPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //disable the send button after send
                sendButton.setEnabled(false);
                homeViewModel.setmText("empty");
            }
        });
        return root;
    }

    // function to show a dialog to select video file
    private void showVideoChooserDialog() {

        final CharSequence[] options = { "From Camera", "From Gallery",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Upload!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("From Camera")) {
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

//                    File f = null;
//                    try {
//                        f = File.createTempFile("temp.mp4", null, Environment.getExternalStorageDirectory());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    File f = new File(android.os.Environment
                            .getExternalStorageDirectory(), "temp.mp4");
                    Uri fileURI = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", f);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileURI);
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("From Gallery")) {
                    // Intent intent = new
                    // Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    // intent.setType("image/*");
                    // startActivityForResult(Intent.createChooser(intent,
                    // "Select File"),2);

                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("video/*");
                    startActivityForResult(intent, 2);
                    //
                    // Intent photoPickerIntent = new
                    // Intent(Intent.ACTION_PICK);
                    // photoPickerIntent.setType("image/*");
                    // startActivityForResult(photoPickerIntent, 2);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    // on activity result to get file from intent data
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {

                File f = new File(Environment.getExternalStorageDirectory()
                        .toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.mp4")) {
                        Log.d("name is onactivity:", f.getName());
                        f = temp;
                        break;
                    }
                }
                 videoPath =  f.getAbsolutePath();
                Log.d("SelectedVideoPathCamera", "videoPath"+videoPath);
                homeViewModel.setmText(videoPath);
                sendButton.setEnabled(true);

            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Video.Media.DATA };
                Cursor c = getContext().getContentResolver().query(selectedImage, filePath,
                        null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                videoPath = c.getString(columnIndex);
                c.close();
                Log.d("SelectedVideoPath", videoPath);
                homeViewModel.setmText(videoPath);
                sendButton.setEnabled(true);



            }
        }
    }



    private void uploadVideo(String videoPath)
    {
        services.newPost( "kz", videoPath,"none" , 0 );
        Log.d("VideoPath uploadVideo", videoPath);
    }

}