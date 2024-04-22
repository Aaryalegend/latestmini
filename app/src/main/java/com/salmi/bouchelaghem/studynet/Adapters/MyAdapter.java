package com.salmi.bouchelaghem.studynet.Adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.salmi.bouchelaghem.studynet.Models.ClassItem;
import com.salmi.bouchelaghem.studynet.R;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<ClassItem> itemList;

    public MyAdapter(List<ClassItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_class, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClassItem item = itemList.get(position);

        holder.txtClassGroup.setText(item.getGroup());
        holder.txtClassStartHour.setText(item.getStartHour());
        holder.txtClassEndHour.setText(item.getEndHour());
        holder.txtClassSubject.setText(item.getSubject());
        holder.txtClassType.setText(item.getType());
        holder.imgBookmark.setVisibility(item.isShowBookmark() ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtClassGroup;
        TextView txtClassStartHour;
        TextView txtClassEndHour;
        TextView txtClassSubject;
        TextView txtClassType;
        ImageView imgBookmark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtClassGroup = itemView.findViewById(R.id.txtClassGroup);
            txtClassStartHour = itemView.findViewById(R.id.txtClassStartHour);
            txtClassEndHour = itemView.findViewById(R.id.txtClassEndHour);
            txtClassSubject = itemView.findViewById(R.id.txtClassSubject);
            txtClassType = itemView.findViewById(R.id.txtClassType);
            imgBookmark = itemView.findViewById(R.id.imgBookmark);
        }
    }
}
