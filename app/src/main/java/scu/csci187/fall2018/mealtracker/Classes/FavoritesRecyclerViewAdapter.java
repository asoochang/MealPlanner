package scu.csci187.fall2018.mealtracker.Classes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import scu.csci187.fall2018.mealtracker.Fragments.FavoritesFragment;
import scu.csci187.fall2018.mealtracker.R;

public class FavoritesRecyclerViewAdapter extends RecyclerView.Adapter<FavoritesRecyclerViewAdapter.MyViewHolder> {

    private List<String> meals;
    private List<String> picUrls;
    FavoritesFragment sourceFragment;
    private ItemClickListener clickListener;
    Context mContext;

    public FavoritesRecyclerViewAdapter(Context context, List<String> meals, List<String> picUrls,
                                        FavoritesFragment sourceFragment) {
        this.meals = meals;
        this.picUrls = picUrls;
        this.mContext = context;
        this.sourceFragment = sourceFragment;
    }

    @Override
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.favorites_item, parent, false);
        final MyViewHolder vHolder = new MyViewHolder(view);

        // create and attach ClickListeners for both image and frame of Favorites item
        vHolder.imView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = vHolder.getAdapterPosition();
                String mealName = meals.get(position);
                sourceFragment.showMealDetail(mealName);
            }
        });
        vHolder.favLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = vHolder.getAdapterPosition();
                String mealName = meals.get(position);
                sourceFragment.showMealDetail(mealName);
            }
        });

        return vHolder;
    }

    // Binds data to recycled views
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.itemName.setText(meals.get(position));
        new ImageLoaderFromUrl(holder.imView).execute(picUrls.get(position));
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }



    // Stores and recycles views as they are scrolled off screen
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imView;
        private TextView itemName;
        private TableLayout favLayout;

        public MyViewHolder(View itemView) {
            super(itemView);

            imView = itemView.findViewById(R.id.favoritesPic);
            itemName = itemView.findViewById(R.id.mealName);
            favLayout = itemView.findViewById(R.id.favItemLayout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "CLICKY", Toast.LENGTH_SHORT).show();
            clickListener.onClick(view, getAdapterPosition()); // call the onClick in the OnItemClickListener
        }

    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onClick(View view, int position);
    }
}
