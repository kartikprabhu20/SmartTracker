package com.component.smarttracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ComponentAdapter extends RecyclerView.Adapter<ComponentAdapter.MyViewHolder>{

    private final Context mContext;
    private List<ComponentTracker> componentList = new ArrayList<>();
    private ItemClicklistener itemClicklistener;


    public ComponentAdapter(Context context, List<ComponentTracker> componentList) {
        mContext = context;
        componentList.addAll(componentList);
        Log.i("Smart_tracker", "Constructor");

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        Log.i("Smart_tracker", "onCreateViewHolder");
        View view=  LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.component_card_view, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.i("Smart_tracker", "onBindViewHolder");

        if (holder != null) {

            ComponentTracker component = componentList.get(position);
            Log.i("Smart_tracker", "onBindViewHolder message:" + component.toString());

            boolean isPhoto = component.getPhotoUrl() != null;
                holder.componentTitleView.setText(component.getComponentName());
                holder.imageView.setVisibility(View.VISIBLE);
                if(isPhoto)
                    Glide.with(holder.imageView.getContext())
                        .load(isPhoto? component.getPhotoUrl():R.drawable.fui_ic_twitter_bird_white_24dp)
                        .into(holder.imageView);

        }
    }

    @Override
    public int getItemCount() {
        Log.i("Smart_tracker", "getItemCount="+ componentList.size());

        if (componentList == null)
            return 0;

        return componentList.size();
    }

    public List<ComponentTracker> getComponentList() {
        return componentList;
    }

    public void addComponent(ComponentTracker componentTracker) {
        componentList.add(componentTracker);
        Log.i("Smart_tracker", "addComponent");
        notifyDataSetChanged();
        Log.i("Smart_tracker", "addComponent getItemCount="+ componentList.size());

    }

    public void updateComponent(ComponentTracker componentTracker) {
        componentList.set(componentList.indexOf(componentTracker), componentTracker);
        Log.i("Smart_tracker", "updateComponent");
        notifyDataSetChanged();
    }

    public void removeComponent(ComponentTracker message) {
        componentList.remove(message);
        notifyDataSetChanged();
    }

    public void clear() {
        Log.i("Smart_tracker", "clear");
        componentList.clear();
        notifyDataSetChanged();
    }

    public void filterComponents(List<ComponentTracker> componentList) {
        this.componentList = componentList;
        notifyDataSetChanged();
    }

    public void attachListener(ItemClicklistener itemClicklistener) {
        this.itemClicklistener = itemClicklistener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        public ImageView imageView;
        public TextView componentTitleView, noComponentMessage;


        public MyViewHolder(View view) {
            super(view);
            Log.i("Smart_tracker", "MyViewHolder");

            this.componentTitleView = view.findViewById(R.id.component_title);
            this.imageView = view.findViewById(R.id.component_image);
//            this.noComponentMessage = view.findViewById(R.id.no_component_message);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClicklistener.onItemClick(v, getAdapterPosition(),false);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClicklistener.onItemClick(v, getAdapterPosition(), true);
            return  true;
        }
    }
}
