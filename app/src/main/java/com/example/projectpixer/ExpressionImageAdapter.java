package com.example.projectpixer;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ExpressionImageAdapter extends RecyclerView.Adapter<ExpressionImageAdapter.ImageViewHolder> {

    private Context mContext;
    private List<ExpressionDetectionUpload> mUploads;
    private OnItemClickListener mListener;

    public ExpressionImageAdapter(Context context, List<ExpressionDetectionUpload> uploads) {
        mContext = context;
        mUploads = uploads;
    }
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.expression_single_recycleview, parent, false);
        return new ImageViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        ExpressionDetectionUpload uploadCurrent = mUploads.get(position);
        holder.textViewName.append("Bounding Polygon " + "(" + uploadCurrent.getBoundingPolygon()+ "\n");
        holder.textViewName.append("Angles of rotation " + uploadCurrent.getAnglesOfRotation()+ "\n");
        holder.textViewName.append("LeftEarPos: " + "(" +uploadCurrent.getLeftEarPos()+ "\n");
        holder.textViewName.append("RightEarPos: " + "(" +uploadCurrent.getRightEarPos()+ "\n");
        holder.textViewName.append("SmileProbability: " +uploadCurrent.getSmileProbability()+ "\n");
        holder.textViewName.append("RightEyeOpenProbability: " +uploadCurrent.getRightEyeOpenProbability()+ "\n");
        holder.textViewName.append("LeftEyeOpenProbability: " +uploadCurrent.getLeftEyeOpenProbability());

        Picasso.get()
                .load(uploadCurrent.getmImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }
    @Override
    public int getItemCount() {
        return mUploads.size();
    }
    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{
        public TextView textViewName;
        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.text_view_name);
            imageView = itemView.findViewById(R.id.image_view_upload);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();

            }
        }
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete");

            delete.setOnMenuItemClickListener(this);
        }
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    switch (item.getItemId()) {

                        case 2:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }
    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}