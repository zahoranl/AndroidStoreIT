package hu.bme.aut.packit.activity.category;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import java.io.Serializable;
import java.util.List;

import hu.bme.aut.packit.R;
import hu.bme.aut.packit.activity.MainActivity;
import hu.bme.aut.packit.activity.item.ItemByCategoryActivity;
import hu.bme.aut.packit.adapter.CategoriesAdapter;
import hu.bme.aut.packit.adapter.ItemsAdapter;
import hu.bme.aut.packit.data.Category;
import hu.bme.aut.packit.data.Item;
import hu.bme.aut.packit.touchHelper.CategoryListTouchHelperCallback;

import static hu.bme.aut.packit.activity.MainActivity.KEY_EDIT;

public class Category_Activity extends AppCompatActivity {
    public static final int REQUEST_NEW_CATEGORY = 105;
    public static final int REQUEST_EDIT_CATEGORY= 106;
    private CategoriesAdapter categoriesAdapter;
    private Category categoryToEditHolder;
    private int  categoryToEditPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_);
        RecyclerView recyclerViewCategory = (RecyclerView)findViewById(R.id.recyclerViewCategory);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        setUpToolBar();

        List<Category> categoryList= Category.listAll(Category.class);
        categoriesAdapter = new CategoriesAdapter(categoryList,this);

        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCategory.setAdapter(categoriesAdapter);

        CategoryListTouchHelperCallback categoryListTouchHelperCallback = new CategoryListTouchHelperCallback(categoriesAdapter,this);
        ItemTouchHelper touchHelper = new ItemTouchHelper(categoryListTouchHelperCallback);
        touchHelper.attachToRecyclerView(recyclerViewCategory);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentStart=new Intent(Category_Activity.this, CreateCategoryActivity.class);
                startActivityForResult(intentStart, MainActivity.REQUEST_NEW_CATEGORY);
            }
        });

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case REQUEST_NEW_CATEGORY:
                        Category categoryTemp = (Category) data.getSerializableExtra(CreateCategoryActivity.KEY);
                        categoriesAdapter.addCategory(categoryTemp);
                        showSnackBarMessage(getString(R.string.category_added_snack));
                        break;
                    case REQUEST_EDIT_CATEGORY:
                        Category itemCategory = (Category) data.getSerializableExtra(CreateCategoryActivity.KEY);
                        if(!categoryToEditHolder.getCategoryName().equals(itemCategory.getCategoryName())){
                            List<Item> itemList= Item.listAll(Item.class);
                            ItemsAdapter ia=new ItemsAdapter(itemList,this,false);

                            for (int i=0;i<itemList.size();i++)
                                if(itemList.get(i).getCategoryName().equals(categoryToEditHolder.getCategoryName())){
                                    itemList.get(i).setCategoryName(itemCategory.getCategoryName());
                                    ia.updateItem(i,itemList.get(i));
                                    categoriesAdapter.notifyDataSetChanged();
                                }
                        }
                        categoryToEditHolder.setCategoryName(itemCategory.getCategoryName());
                        categoryToEditHolder.setMegjegyzes(itemCategory.getMegjegyzes());
                        categoryToEditHolder.setCode(itemCategory.getCode());
                        if (categoryToEditPosition != -1) {
                            categoriesAdapter.updateCategory(categoryToEditPosition, categoryToEditHolder);
                            categoryToEditPosition = -1;
                        } else {
                            categoriesAdapter.notifyDataSetChanged();
                        }
                        break;
                }
                break;
            case RESULT_CANCELED:
                showSnackBarMessage(getString(R.string.proc_canceled_snack));
                break;
        }
    }

    public void showEditCategoryActivity(Category categoryToEdit, int position) {
        Intent intentStart = new Intent(Category_Activity.this, CreateCategoryActivity.class);
        categoryToEditHolder = categoryToEdit;
        categoryToEditPosition = position;
        intentStart.putExtra(KEY_EDIT,  categoryToEdit);
        startActivityForResult(intentStart, REQUEST_EDIT_CATEGORY);
    }
    public void showMoreCategoryActivity(Category categoryToSee){
        List<Item> itemsListByCategory=Item.listAll(Item.class);
        for (int i=0;i<itemsListByCategory.size();i++)
            if (!itemsListByCategory.get(i).getCategoryName().equals(categoryToSee.getCategoryName()))
                itemsListByCategory.remove(i--);
        Intent intentStart = new Intent(Category_Activity.this, ItemByCategoryActivity.class);
        intentStart.putExtra("DETAILS", (Serializable) itemsListByCategory);
        intentStart.putExtra("CAT", categoryToSee);
        startActivity(intentStart);
    }
    public void enableDelete(Category category, final int adapterPosition) {
        List<Item> itemList=Item.listAll(Item.class);
        int count=0;
        for (int i=0 ; i<itemList.size();i++)
            if (itemList.get(i).getCategoryName().equals(category.getCategoryName())) count++;
        if (count>0){
            new AlertDialog.Builder(Category_Activity.this)
                    .setTitle(R.string.not_enabled)
                    .setMessage(R.string.delete_only_empty_cat)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            categoriesAdapter.notifyDataSetChanged();
                            return;
                        }
                    }).show();
        }else {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setMessage(R.string.are_u_sure_delete_item);
            adb.setTitle(R.string.delete);
            adb.setIcon(android.R.drawable.ic_dialog_alert);
            adb.setCancelable(false);
            adb.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    categoriesAdapter.removeCategory(adapterPosition);
                }
            });
            adb.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    categoriesAdapter.notifyDataSetChanged();
                    return;
                }
            });
            adb.show();
        }
    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void showSnackBarMessage(String message) {
        Snackbar.make((CoordinatorLayout) findViewById(R.id.layoutContent), message, Snackbar.LENGTH_LONG).show();
    }


}
