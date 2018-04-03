package hu.bme.aut.packit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import hu.bme.aut.packit.R;
import hu.bme.aut.packit.activity.category.Category_Activity;
import hu.bme.aut.packit.data.Category;
import hu.bme.aut.packit.data.Item;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

public static class ViewHolder extends RecyclerView.ViewHolder {
    public ImageView ivIcon;
    public TextView tvCategory;
    public TextView tvMegjegyzes;
    public TextView tvDb;
    public ImageButton btnEdit;
    public ImageButton btnMore;


    public ViewHolder(View itemView) {
        super(itemView);
        ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
        tvCategory = (TextView) itemView.findViewById(R.id.tvTitle);
        tvMegjegyzes= (TextView) itemView.findViewById(R.id.tvLine);
        btnEdit = (ImageButton) itemView.findViewById(R.id.btnEdit);
        btnMore=(ImageButton) itemView.findViewById(R.id.btnMore);
        tvDb = (TextView) itemView.findViewById(R.id.tvDb);
        TextView tvManyHead= (TextView) itemView.findViewById(R.id.tvManyHead);
        tvManyHead.setText(R.string.count);
    }
}
    private List<Category> categoryList;
    private Context context;
    private int lastPosition = -1;

    public CategoriesAdapter(List<Category> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder,final int position) {
        viewHolder.ivIcon.setImageResource(R.mipmap.ic_category);
        viewHolder.tvCategory.setText(categoryList.get(position).getCategoryName());
        viewHolder.tvMegjegyzes.setText(categoryList.get(position).getMegjegyzes());
        viewHolder.tvDb.setText(Integer.toString(CategoryItemCount(position)));
       viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              ((Category_Activity) context).showEditCategoryActivity(categoryList.get(viewHolder.getAdapterPosition()), viewHolder.getAdapterPosition());
           }
       });
        viewHolder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Category_Activity) context).showMoreCategoryActivity(categoryList.get(viewHolder.getAdapterPosition()));
            }
        });

        setAnimation(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
    public void addCategory(Category category) {
        category.save();
        categoryList.add(category);
        notifyDataSetChanged();
    }
    public void updateCategory(int index, Category category) {
        categoryList.set(index, category);
        category.save();
        notifyItemChanged(index);
    }
    public void removeCategory(int index) {
        categoryList.get(index).delete();
        categoryList.remove(index);
        notifyDataSetChanged();
    }
    public void swapCategorys(int oldPosition, int newPosition) {
        if (oldPosition < newPosition) {
            for (int i = oldPosition; i < newPosition; i++) {
                Collections.swap(categoryList, i, i + 1);
            }
        } else {
            for (int i = oldPosition; i > newPosition; i--) {
                Collections.swap(categoryList, i, i - 1);
            }
        }
        notifyItemMoved(oldPosition, newPosition);
    }
    public Category getCategory(int i) {
        return categoryList.get(i);
    }

    public int CategoryItemCount(int position){
        int sum=0;
        List<Item> itemList = Item.listAll(Item.class);
        for (int i=0;i<itemList.size();i++){
            if (itemList.get(i).getCategoryName().equals(categoryList.get(position).getCategoryName()))
                sum++;
        }
        return sum;
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}
