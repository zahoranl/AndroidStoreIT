package hu.bme.aut.packit.touchHelper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import hu.bme.aut.packit.activity.MainActivity;
import hu.bme.aut.packit.adapter.ItemsAdapter;

public class ItemsListTouchHelperCallback extends ItemTouchHelper.Callback {
    private ItemsAdapter adapter;
    private Context context;

    public ItemsListTouchHelperCallback(ItemsAdapter adapter,Context context) {
        this.adapter = adapter;
        this.context=context;
    }
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

        return makeMovementFlags(dragFlags, swipeFlags);
    }
    @Override
    public boolean onMove(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        adapter.swapItems(viewHolder.getAdapterPosition(),
                target.getAdapterPosition());

        return true;
    }
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        ((MainActivity) context).enableDeleteItem(viewHolder.getAdapterPosition());
    }
}
