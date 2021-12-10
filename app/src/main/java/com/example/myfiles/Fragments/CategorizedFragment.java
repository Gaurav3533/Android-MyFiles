package com.example.myfiles.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfiles.FileAdapter;
import com.example.myfiles.FileOpener;
import com.example.myfiles.OnFileSelectedListener;
import com.example.myfiles.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CategorizedFragment extends Fragment implements OnFileSelectedListener {

    View view;
    private FileAdapter fileAdapter;
    private RecyclerView recyclerView;
    private List<File> fileList;
    String[] items = {"Details","Rename","Delete","Share"};
    File storage;
    String data;
    File path;
    public static final int Request_code = 1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_categorized,container,false);
        Bundle bundle = this.getArguments();
        if (bundle.getString("fileType").equals("downloads")){
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        }
        else {
            path = Environment.getExternalStorageDirectory();
        }

        runtimepermission();

        return view;
    }
    private void runtimepermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}
                    , Request_code);
        }
        else {
            displayFiles();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Request_code){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                displayFiles();
            }
            else{
                requestPermissions(new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        , Request_code);
            }
        }
    }

    public ArrayList<File> findFile(File file){
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null && files.length > 0) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    arrayList.addAll(findFile(singleFile));
                } else {
                    switch (getArguments().getString("fileType")) {
                        case "image":
                            if (singleFile.getName().toLowerCase().endsWith(".jpeg") || singleFile.getName().toLowerCase().endsWith(".jpg") ||
                                    singleFile.getName().toLowerCase().endsWith(".png")) {
                                arrayList.add(singleFile);
                            }
                            break;
                        case "video":
                            if (singleFile.getName().toLowerCase().endsWith(".mp4")) {
                                arrayList.add(singleFile);
                            }
                            break;
                        case "music":
                            if (singleFile.getName().toLowerCase().endsWith(".mp3") ||
                                    singleFile.getName().toLowerCase().endsWith(".wav")) {
                                arrayList.add(singleFile);
                            }
                            break;
                        case "docs":
                            if (singleFile.getName().toLowerCase().endsWith(".pdf") ||
                                    singleFile.getName().toLowerCase().endsWith(".doc")) {
                                arrayList.add(singleFile);
                            }
                            break;
                        case "apk":
                            if (singleFile.getName().toLowerCase().endsWith(".apk")) {
                                arrayList.add(singleFile);
                            }
                            break;
                        case "downloads":
                            if (singleFile.getName().toLowerCase().endsWith(".jpeg") || singleFile.getName().toLowerCase().endsWith(".jpg") ||
                                    singleFile.getName().toLowerCase().endsWith(".png") || singleFile.getName().toLowerCase().endsWith(".mp3") ||
                                    singleFile.getName().toLowerCase().endsWith(".mp4") || singleFile.getName().toLowerCase().endsWith(".pdf") ||
                                    singleFile.getName().toLowerCase().endsWith(".wav") || singleFile.getName().toLowerCase().endsWith(".doc") ||
                                    singleFile.getName().toLowerCase().endsWith(".apk")) {
                                arrayList.add(singleFile);
                            }
                            break;
                    }
                }
            }
        }

        return arrayList;
    }

    private void displayFiles(){
        recyclerView = view.findViewById(R.id.recycler_internal);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fileList = new ArrayList<>();
        fileList.addAll(findFile(path));
        fileAdapter = new FileAdapter(getContext(),fileList,this);
        recyclerView.setAdapter(fileAdapter);
    }

    @Override
    public void onFileClicked(File file) {
        if (file.isDirectory()){
            Bundle bundle = new Bundle();
            bundle.putString("path",file.getAbsolutePath());
            CategorizedFragment internalFragment = new CategorizedFragment();
            internalFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container,internalFragment).addToBackStack(null).commit();

        }
        //Then it will be a file
        else {
            try {
                FileOpener.OpenFile(getContext(),file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFileLongClicked(File file, int position) {
        final Dialog optionDialog = new Dialog(getContext());
        optionDialog.setContentView(R.layout.option_dialog);
        optionDialog.setTitle("Select Options");

        ListView options = (ListView)optionDialog.findViewById(R.id.List);
        CustomAdapter customAdapter = new CustomAdapter();
        options.setAdapter(customAdapter);
        optionDialog.show();

        options.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                switch (selectedItem){
                    case "Details":
                        AlertDialog.Builder detailDialog = new AlertDialog.Builder(getContext());
                        detailDialog.setTitle("Details");
                        final TextView details = new TextView(getContext());
                        detailDialog.setView(details);
                        Date lastModified = new Date(file.lastModified());
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyyy  HH:mm:ss");
                        String formattedDate = formatter.format(lastModified);

                        details.setText("File Name: "+ file.getName() + "\n" +
                                "Size: "+ Formatter.formatShortFileSize(getContext(),file.length()) + "\n" +
                                "File Path: "+file.getAbsolutePath() + "\n" +
                                "Last Modified: "+formattedDate);

                        detailDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                optionDialog.cancel();
                            }
                        });
                        AlertDialog alertDialog_details = detailDialog.create();
                        alertDialog_details.show();
                        break;

                    case "Rename":
                        AlertDialog.Builder renameDialog = new AlertDialog.Builder(getContext());
                        renameDialog.setTitle("Rename File:");
                        final EditText name = new EditText(getContext());
                        renameDialog.setView(name);

                        renameDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newName = name.getEditableText().toString();
                                int lastIndex = file.getAbsolutePath().lastIndexOf(".");
                                String extension = file.getAbsolutePath().substring(lastIndex);
                                File current = new File(file.getAbsolutePath());
                                File destination = new File(file.getAbsolutePath().replace(file.getName(), newName) + extension);
                                if (current.renameTo(destination)){
                                    fileList.set(position,destination);
                                    fileAdapter.notifyItemChanged(position);
                                    Toast.makeText(getContext(), "File renamed", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(getContext(), "Couldn't rename", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        renameDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                optionDialog.cancel();
                            }
                        });
                        AlertDialog alertDialog_rename = renameDialog.create();
                        alertDialog_rename.show();
                        break;

                    case "Delete":
                        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getContext());
                        deleteDialog.setTitle("Delete: "+ file.getName() + " ?");
                        deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                file.delete();
                                fileList.remove(position);
                                fileAdapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "File Deleted", Toast.LENGTH_SHORT).show();
                            }
                        });

                        deleteDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                optionDialog.cancel();
                            }
                        });

                        AlertDialog alertDialog_delete = deleteDialog.create();
                        alertDialog_delete.show();
                        break;

                    case "Share":
                        String fileName = file.getName();
                        Intent share = new Intent();
                        share.setAction(Intent.ACTION_SEND);
                        share.setType("*/*");
                        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        startActivity(Intent.createChooser(share,"Share "+fileName));
                        break;
                }
            }
        });

    }

    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return items[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View myView = getLayoutInflater().inflate(R.layout.option_layout,null);
            TextView txtOptions = myView.findViewById(R.id.txtOption);
            ImageView imgOptions = myView.findViewById(R.id.imgOption);
            txtOptions.setText(items[position]);

            if (items[position].equals("Details")){
                imgOptions.setImageResource(R.drawable.ic_details);
            } else if (items[position].equals("Rename")) {
                imgOptions.setImageResource(R.drawable.ic_rename);
            } else if (items[position].equals("Delete")){
                imgOptions.setImageResource(R.drawable.ic_delete);
            }else if (items[position].equals("Share")){
                imgOptions.setImageResource(R.drawable.ic_share);
            }

            return myView;
        }
    }
}
