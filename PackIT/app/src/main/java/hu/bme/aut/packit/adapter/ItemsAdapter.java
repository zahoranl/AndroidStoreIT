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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hu.bme.aut.packit.R;
import hu.bme.aut.packit.activity.MainActivity;
import hu.bme.aut.packit.activity.item.ItemByCategoryActivity;
import hu.bme.aut.packit.data.Item;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivIcon;
        public TextView tvItem;
        public TextView tvCategory;
        public TextView tvDarab;
        public ImageButton btnEdit;
        public ImageButton btnMore;

        public ViewHolder(View itemView) {
            super(itemView);
            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
            tvItem = (TextView) itemView.findViewById(R.id.tvTitle);
            tvCategory = (TextView) itemView.findViewById(R.id.tvLine);
            tvDarab = (TextView) itemView.findViewById(R.id.tvDb);
            btnEdit = (ImageButton) itemView.findViewById(R.id.btnEdit);
            btnMore = (ImageButton) itemView.findViewById(R.id.btnMore);
        }
    }

    private List<Item> itemsList;
    private Context context;
    private int lastPosition = -1;
    private boolean editable;

    public ItemsAdapter(List<Item> itemsList, Context context, boolean editable) {
        this.itemsList = new ArrayList<Item>(itemsList);
        this.context = context;
        this.editable=editable;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder,final int position) {
        viewHolder.tvItem.setText(itemsList.get(position).getItemName());
        viewHolder.tvCategory.setText(itemsList.get(position).getCategoryName());
        viewHolder.tvDarab.setText(Integer.toString(itemsList.get(position).getDarab()));
        viewHolder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ((MainActivity) context).showMoreItemActivity(itemsList.get(viewHolder.getAdapterPosition()));
                }catch (Exception e) {
                    ((ItemByCategoryActivity) context).showItemDetails(itemsList.get(viewHolder.getAdapterPosition()));
                }
            }
        });
        viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).showEditItemActivity(itemsList.get(viewHolder.getAdapterPosition()), viewHolder.getAdapterPosition());
            }
        });

        if (!editable) {
            viewHolder.btnEdit.setVisibility(View.GONE);
        }
        setAnimation(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }
    public void addItem(Item item) {
        item.save();
        itemsList.add(item);
        notifyDataSetChanged();
    }
    public void updateItem(int index, Item item) {
        itemsList.set(index, item);
        item.save();
        notifyItemChanged(index);
    }
    public void removeItem(int index) {
        itemsList.get(index).delete();
        itemsList.remove(index);
        notifyDataSetChanged();
    }
    public void removeAll(){
        itemsList.removeAll(itemsList);
    }
    public int getIndex(Item item){
        int ret=-1;
            for(int i=0;i< itemsList.size();i++){
                if (itemsList.get(i).getCode().equals(item.getCode()))
                    ret=i;
            }
        return ret;
    }
    public void swapItems(int oldPosition, int newPosition) {
        if (oldPosition < newPosition) {
            for (int i = oldPosition; i < newPosition; i++) {
                Collections.swap(itemsList, i, i + 1);
            }
        } else {
            for (int i = oldPosition; i > newPosition; i--) {
                Collections.swap(itemsList, i, i - 1);
            }
        }
        notifyItemMoved(oldPosition, newPosition);
    }
    public Item getItem(int i) {
        return itemsList.get(i);
    }
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}
