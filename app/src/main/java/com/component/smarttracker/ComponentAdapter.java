package com.component.smarttracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ComponentAdapter extends RecyclerView.Adapter<ComponentAdapter.MyViewHolder> implements Filterable {

    private final Context mContext;
    private List<ComponentTracker> filteredList = new ArrayList<>();
    private List<ComponentTracker> componentFullList = new ArrayList<>();
    private ItemClicklistener itemClicklistener;
    private ComponentListListener componentListListener;

    public ComponentAdapter(Context context, List<ComponentTracker> componentList) {
        mContext = context;
        filteredList.addAll(componentList);
        componentFullList.addAll(componentList);
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

            ComponentTracker component = filteredList.get(position);
            Log.i("Smart_tracker", "onBindViewHolder message:" + component.toString());

            boolean isPhoto = component.getPhotoUrl() != null;
                holder.componentTitleView.setText(component.getComponentName());
//                holder.imageView.setVisibility(View.VISIBLE);
//                if(isPhoto)
//                    Glide.with(holder.imageView.getContext())
//                        .load(isPhoto? component.getPhotoUrl():R.drawable.fui_ic_twitter_bird_white_24dp)
//                        .into(holder.imageView);

        }
    }

    @Override
    public int getItemCount() {
        Log.i("Smart_tracker", "getItemCount="+ filteredList.size());

        if (filteredList == null)
            return 0;

        return filteredList.size();
    }

    public List<ComponentTracker> getComponentList() {
        return filteredList;
    }

    public void addComponent(ComponentTracker componentTracker) {
        filteredList.add(componentTracker);
        componentFullList.add(componentTracker);
        notifyDataSetChanged();
        componentListListener.checkComponentList();
    }

    public void updateComponent(ComponentTracker componentTracker) {
        filteredList.set(filteredList.indexOf(componentTracker), componentTracker);
        componentFullList.set(componentFullList.indexOf(componentTracker), componentTracker);
        Log.i("Smart_tracker", "updateComponent");
        notifyDataSetChanged();
        componentListListener.checkComponentList();
    }

    public void removeComponent(ComponentTracker message) {
        filteredList.remove(message);
        componentFullList.remove(message);
        notifyDataSetChanged();
        componentListListener.checkComponentList();
    }

    public void clear() {
        Log.i("Smart_tracker", "clear");
        filteredList.clear();
        componentFullList.clear();
        notifyDataSetChanged();
        componentListListener.checkComponentList();
    }

    public void attachListener(ItemClicklistener itemClicklistener) {
        this.itemClicklistener = itemClicklistener;
    }

    public void attachComponentListListeener(ComponentListListener componentListListener) {
        this.componentListListener = componentListListener;
    }

    @Override
    public Filter getFilter() {
        return customFilter;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        public ImageView imageView;
        public TextView componentTitleView, noComponentMessage;


        public MyViewHolder(View view) {
            super(view);
            Log.i("Smart_tracker", "MyViewHolder");

            this.componentTitleView = view.findViewById(R.id.component_title);
//            this.imageView = view.findViewById(R.id.component_image);
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


    private Filter customFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ComponentTracker> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(componentFullList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ComponentTracker component : componentFullList) {
                    if (component.getComponentName().toLowerCase().contains(filterPattern)
                            || component.getComponentKey().toLowerCase().contains(filterPattern)
                            || (null != component.getLender() && component.getLender().toLowerCase().contains(filterPattern))
                            || (null != component.getBorrower() && component.getBorrower().toLowerCase().contains(filterPattern))
                            || (null != component.getBorrowerTeam() && component.getBorrowerTeam().toLowerCase().contains(filterPattern))
                            || (null != component.getLenderTeam() && component.getLenderTeam().toLowerCase().contains(filterPattern))
                    ) {
                        filteredList.add(component);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList.clear();
            filteredList.addAll((List) results.values);
            notifyDataSetChanged();
            componentListListener.checkComponentList();
        }
    };

    public interface ComponentListListener {
        void checkComponentList();
    }
}
