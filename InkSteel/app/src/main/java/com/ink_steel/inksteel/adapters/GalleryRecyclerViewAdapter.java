package com.ink_steel.inksteel.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.FullScreenImageActivity;
import com.ink_steel.inksteel.activities.GalleryActivity;
import com.ink_steel.inksteel.helpers.ConstantUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.GalleryViewHolders> {

    private ArrayList<Uri> images;
    private Context context;

    public GalleryRecyclerViewAdapter(Context context, ArrayList<Uri> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public void onBindViewHolder(final GalleryViewHolders holder, int pos) {
        final int position = pos;
        Picasso.with(context)
                .load(images.get(position))
                .transform(new CropCircleTransformation())
                .resize(600, 690)
                .into(holder.image);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (v.isLongClickable()) {
                    equalPosition(position);
                    return true;
                }
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FullScreenImageActivity.class);
                intent.putExtra("image", position);
                context.startActivity(intent);
            }
        });
    }

    private void equalPosition(int pos) {
        final int position = pos;
        ConstantUtils.FIRESTORE_GALLERY_REFERNENCE.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (("{picture=" + String.valueOf(images.get(position)) + "}").equals(document.getData().toString())) {
                                    final DocumentReference docRef = ConstantUtils.FIRESTORE_GALLERY_REFERNENCE.document(document.getId());
                                    setAlert(docRef);
                                    break;
                                }
                            }
                        }
                    }
                });
    }

    private void setAlert(DocumentReference doc) {
        final DocumentReference docRef = doc;
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setMessage("Do you want to delete the image?")
                .setCancelable(true)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        docRef.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "Image successfully deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
        AlertDialog ad = alertBuilder.create();
        ad.setTitle("WARNING MESSAGE");
        ad.setIcon(R.drawable.warning_msg);
        ad.show();
    }

    @Override
    public GalleryViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_list, parent, false);
        GalleryViewHolders vh = new GalleryViewHolders(v);
        return vh;
    }

    @Override
    public int getItemCount() {
        return this.images.size();
    }

    public class GalleryViewHolders extends RecyclerView.ViewHolder {

        public ImageView image;

        public GalleryViewHolders(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}