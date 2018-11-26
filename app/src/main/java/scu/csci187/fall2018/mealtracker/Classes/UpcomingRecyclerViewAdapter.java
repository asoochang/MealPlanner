package scu.csci187.fall2018.mealtracker.Classes;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import scu.csci187.fall2018.mealtracker.Fragments.HomeFragment;
import scu.csci187.fall2018.mealtracker.R;

public class UpcomingRecyclerViewAdapter extends RecyclerView.Adapter<UpcomingRecyclerViewAdapter.MyViewHolder> {

    private List<String> meals, dates, picUrls, bookmarkURL;
    private List<Integer> blds;
    private HomeFragment sourceFragment;
    private ItemClickListener clickListener;
    Context mContext;

    public UpcomingRecyclerViewAdapter(Context context, List<String> meals, List<String> dates,
                                       List<String> picUrls, List<String> bookmarkURL, List<Integer> blds, HomeFragment sourceFragment) {
        this.meals = meals;
        this.dates = dates;
        this.picUrls = picUrls;
        this.bookmarkURL = bookmarkURL;
        this.sourceFragment = sourceFragment;
        this.mContext = context;
        this.blds = blds;
    }

    @Override
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        final MyViewHolder vHolder = new MyViewHolder(view);

        // Setup click listeners for both image and frame of home screen item
        vHolder.imView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = vHolder.getAdapterPosition();
                sourceFragment.showUpcomingMealDetail(bookmarkURL.get(position), position);
            }
        });

        vHolder.homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = vHolder.getAdapterPosition();
                sourceFragment.showUpcomingMealDetail(bookmarkURL.get(position), position);
            }
        });
        return vHolder;
    }

    // Binds data to recycled views
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.itemName.setText(meals.get(position));
        holder.itemDate.setText(dates.get(position));
        int timeOfDay = blds.get(position);
        switch(timeOfDay) {
            case 0: holder.bldText.setText("breakfast");
                break;
            case 1: holder.bldText.setText("lunch");
                break;
            case 2: holder.bldText.setText("dinner");
        }

        new ImageLoaderFromUrl(holder.imView).execute(picUrls.get(position));
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    // Stores and recycles views as they are scrolled off screen
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imView;
        private TextView itemDate;
        private TextView itemName;
        private TextView bldText;
        private LinearLayout homeLayout;

        public MyViewHolder(View itemView) {
            super(itemView);

            imView = itemView.findViewById(R.id.listItemPic);
            itemDate = itemView.findViewById(R.id.listItemDate);
            itemName = itemView.findViewById(R.id.listItemName);
            bldText = itemView.findViewById(R.id.bldText);
            homeLayout = itemView.findViewById(R.id.homeLinearLayout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getAdapterPosition()); // call the onClick in the OnItemClickListener
        }

    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onClick(View view, int position);
    }
}