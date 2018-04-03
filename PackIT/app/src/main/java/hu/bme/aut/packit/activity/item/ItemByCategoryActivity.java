package hu.bme.aut.packit.activity.item;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.packit.R;
import hu.bme.aut.packit.adapter.ItemsAdapter;
import hu.bme.aut.packit.data.Category;
import hu.bme.aut.packit.data.Item;

public class ItemByCategoryActivity extends AppCompatActivity {
    private ItemsAdapter itemsAdapterByCategory;
    private Intent intent;

    private EditText etSearch;
    private RecyclerView recyclerViewCategory;

    private Category category;
    private List<Item> MaxItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_by_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        intent = getIntent();
        category = (Category) intent.getSerializableExtra("CAT");
        MaxItemList= (List<Item>) intent.getSerializableExtra("DETAILS");

        itemsAdapterByCategory = new ItemsAdapter(MaxItemList,this,false);
        recyclerViewCategory = (RecyclerView)findViewById(R.id.recyclerViewCategory);
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCategory.setAdapter(itemsAdapterByCategory);
        setUpToolBar();
        etSearch = (EditText) findViewById(R.id.etSearch);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                if(!etSearch.getText().toString().isEmpty()){
                    List<Item> find=Shearch(etSearch.getText().toString());
                    if (find!=null && find.size()!=0){
                    itemsAdapterByCategory = new ItemsAdapter(find,ItemByCategoryActivity.this,false);
                    recyclerViewCategory.setAdapter(itemsAdapterByCategory);
                    itemsAdapterByCategory.notifyDataSetChanged();
                    } else{
                        showSnackBarMessage(getString(R.string.no_match));
                        itemsAdapterByCategory.removeAll();
                        itemsAdapterByCategory.notifyDataSetChanged();
                    }
                }else {
                    itemsAdapterByCategory = new ItemsAdapter(MaxItemList,ItemByCategoryActivity.this,false);
                    recyclerViewCategory.setAdapter(itemsAdapterByCategory);
                    itemsAdapterByCategory.notifyDataSetChanged();
                }
            }
        });
    }

    public void showItemDetails(Item itemToSee){
        Intent intentStart = new Intent(ItemByCategoryActivity.this, ItemDetails.class);
        intentStart.putExtra("DETAILS", itemToSee);
        startActivity(intentStart);
    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getString(R.string.items);
        if (category!=null) toolbar.setTitle(category.getCategoryName()+" "+ getString(R.string.items) );
        else toolbar.setTitle(getString(R.string.search));
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private List<Item> Shearch(String key){
        List<Item> returnList = new ArrayList<Item>();
        for (int i=0;i<MaxItemList.size();i++)
            if (MaxItemList.get(i).getItemName().toLowerCase().contains(key.toLowerCase()))
                returnList.add(MaxItemList.get(i));
        return returnList;
    }
    private void showSnackBarMessage(String message) {
        Snackbar.make( findViewById(R.id.ItemWithSearchLayout), message,Snackbar.LENGTH_SHORT).show();
    }
}
