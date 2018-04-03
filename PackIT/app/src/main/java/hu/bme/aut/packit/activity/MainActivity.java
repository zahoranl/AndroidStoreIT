package hu.bme.aut.packit.activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import java.io.Serializable;
import java.util.List;
import com.google.android.gms.vision.barcode.Barcode;
import hu.bme.aut.packit.R;
import hu.bme.aut.packit.activity.category.Category_Activity;
import hu.bme.aut.packit.activity.category.CreateCategoryActivity;
import hu.bme.aut.packit.activity.item.CreateItemActivity;
import hu.bme.aut.packit.activity.item.ItemByCategoryActivity;
import hu.bme.aut.packit.activity.item.ItemDetails;
import hu.bme.aut.packit.adapter.CategoriesAdapter;
import hu.bme.aut.packit.adapter.ItemsAdapter;
import hu.bme.aut.packit.data.Category;
import hu.bme.aut.packit.data.Item;
import hu.bme.aut.packit.touchHelper.ItemsListTouchHelperCallback;

import static hu.bme.aut.packit.R.id.RecyclerViewItems;


public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_NEW_ITEM     = 103;
    public static final int REQUEST_EDIT_ITEM    = 104;
    public static final int REQUEST_NEW_CATEGORY = 105;
    public static final int REQUEST_EDIT_CATEGORY= 106;
    public static final int REQUEST_FIND_ITEM    = 110;
    public static final int REQUEST_FIND_CATEGORY= 111;
    public static final int REQUEST_FIND_NOTHING = 112;
    public static final int REQUEST_CODE_QRCODE = 100;
    public static final int REQUEST_NEW_ITEM_BY_QRCODE = 120;
    public static final int REQUEST_CATEGORY_CHANGED = 300;
    public static final int PERMISSION_REQUEST = 200;
    public static final String KEY_EDIT = "KEY_EDIT";
    public static final String CAMERA = "android.permission.CAMERA";

    private CoordinatorLayout layoutContent;
    private DrawerLayout drawerLayout;
    private Context context;
    private RecyclerView recyclerViewItems;

    private ItemsAdapter itemsAdapter;
    private List<Item> itemList;
    private Item itemToEditHolder;
    private int  itemToEditPosition = -1;

    private CategoriesAdapter categoriesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=getApplicationContext();

        List<Item> itemList = Item.listAll(Item.class);
        List<Category> categoryList= Category.listAll(Category.class);

        this.itemList=itemList;
        itemsAdapter = new ItemsAdapter(itemList, this, true);
        categoriesAdapter = new CategoriesAdapter(categoryList,this);

        recyclerViewItems = (RecyclerView)findViewById(RecyclerViewItems);
        layoutContent = (CoordinatorLayout) findViewById(R.id.layoutContent);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.btnAdd);

        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItems.setAdapter(itemsAdapter);

        ItemsListTouchHelperCallback touchHelperCallback = new ItemsListTouchHelperCallback(itemsAdapter, this);
        ItemTouchHelper touchHelper = new ItemTouchHelper(touchHelperCallback);
        touchHelper.attachToRecyclerView(recyclerViewItems);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateItemActivity();
            }
        });

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_about:
                                showSnackBarMessage("Szia!");
                                drawerLayout.closeDrawer(GravityCompat.START);
                                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                                break;
                            case R.id.action_help:
                                showSnackBarMessage(getString(R.string.i_hope_ihelp_snack));
                                drawerLayout.closeDrawer(GravityCompat.START);
                                startActivity(new Intent(MainActivity.this, HelpActivity.class));
                                break;
                            case R.id.action_qrRead:
                                if(ContextCompat.checkSelfPermission(MainActivity.this, CAMERA) != PackageManager.PERMISSION_GRANTED){
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{CAMERA}, PERMISSION_REQUEST);
                                }
                                if(ContextCompat.checkSelfPermission(MainActivity.this, CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                                    startActivityForResult(intent, REQUEST_CODE_QRCODE);
                                    drawerLayout.closeDrawer(GravityCompat.START);
                                }
                                break;
                            case R.id.action_add_item:
                                    showCreateItemActivity();
                                    drawerLayout.closeDrawer(GravityCompat.START);
                                break;
                            case R.id.action_add_category:
                                showCreateCategoryActivity();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                break;
                            case R.id.action_category_view:
                                Intent intentStart=new Intent(MainActivity.this, Category_Activity.class);
                                startActivityForResult(intentStart,REQUEST_CATEGORY_CHANGED);
                                break;
                            case R.id.action_search:
                                intentStart = new Intent(MainActivity.this, ItemByCategoryActivity.class);
                                intentStart.putExtra("DETAILS", (Serializable) Item.listAll(Item.class));
                                intentStart.putExtra("CAT", (Category) null);
                                startActivity(intentStart);
                                break;
                        }
                        return true;
                    }
                });
        setUpToolBar();
    }

    @Override
    public void onResume(){
        super.onResume();
        itemsAdapter=new ItemsAdapter(Item.listAll(Item.class),this, true);
        recyclerViewItems.setAdapter(itemsAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_QRCODE);
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    showSnackBarMessage(getString(R.string.permission_needed));
                }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case REQUEST_NEW_ITEM:
                        Item item = (Item) data.getSerializableExtra(CreateItemActivity.KEY);
                        itemsAdapter.addItem(item);
                        showSnackBarMessage(getString(R.string.item_added_snack));
                        break;
                    case REQUEST_NEW_CATEGORY:
                        Category categoryTemp = (Category) data.getSerializableExtra(CreateCategoryActivity.KEY);
                        categoriesAdapter.addCategory(categoryTemp);
                        showSnackBarMessage(getString(R.string.category_added_snack));
                        break;
                    case REQUEST_EDIT_ITEM:
                        Item itemTemp = (Item) data.getSerializableExtra(CreateItemActivity.KEY);
                        itemToEditHolder.setItemName(itemTemp.getItemName());
                        itemToEditHolder.setCategoryName(itemTemp.getCategoryName());
                        itemToEditHolder.setMegjegyzes(itemTemp.getMegjegyzes());
                        itemToEditHolder.setElhelyezes(itemTemp.getElhelyezes());
                        itemToEditHolder.setCode(itemTemp.getCode());
                        itemToEditHolder.setDarab(itemTemp.getDarab());
                        if (itemToEditPosition != -1) {
                            itemsAdapter.updateItem(itemToEditPosition, itemToEditHolder);
                            itemToEditPosition = -1;
                        } else {
                            itemsAdapter.notifyDataSetChanged();
                        }
                        showSnackBarMessage(getString(R.string.item_modified));
                        break;
                    case REQUEST_EDIT_CATEGORY: break;
                    case REQUEST_FIND_ITEM: break;
                    case REQUEST_FIND_CATEGORY: break;
                    case REQUEST_FIND_NOTHING: break;
                    case REQUEST_CODE_QRCODE:
                        if (data != null) {
                            final Barcode barcode = data.getParcelableExtra("barcode");
                            showSnackBarMessage(barcode.displayValue);

                            Intent intent = new Intent(MainActivity.this, QRReturnActivity.class);
                            intent.putExtra("QRCODE",barcode.displayValue);
                            startActivityForResult(intent,REQUEST_NEW_ITEM_BY_QRCODE);
                            break;
                        }else{
                            itemsAdapter.notifyDataSetChanged();
                            showSnackBarMessage(getString(R.string.proc_comp_snack));
                            break;
                        }
                    case REQUEST_NEW_ITEM_BY_QRCODE:
                        if (data != null) {
                            Item ScanedItem = (Item) data.getSerializableExtra(CreateItemActivity.KEY);
                            itemsAdapter.addItem(ScanedItem);
                            showSnackBarMessage(getString(R.string.item_added_snack));
                            break;
                        } else {
                            itemsAdapter.notifyDataSetChanged();
                            showSnackBarMessage(getString(R.string.proc_comp_snack));
                            break;
                        }
                }
                break;
            case RESULT_CANCELED:
                itemsAdapter=new ItemsAdapter(Item.listAll(Item.class),this, true);
                itemsAdapter.notifyDataSetChanged();
                showSnackBarMessage(getString(R.string.proc_canceled_snack));
                break;
        }
    }

    private void showCreateItemActivity(){
        Intent intentStart=new Intent(MainActivity.this, CreateItemActivity.class);
        startActivityForResult(intentStart, REQUEST_NEW_ITEM);
    }
    public void showEditItemActivity(Item itemToEdit, int position) {
        Intent intentStart = new Intent(MainActivity.this, CreateItemActivity.class);
        itemToEditHolder = itemToEdit;
        itemToEditPosition = position;
        intentStart.putExtra(KEY_EDIT, itemToEdit);
        startActivityForResult(intentStart, REQUEST_EDIT_ITEM);
    }
    public void showMoreItemActivity(Item itemToSee){
        Intent intentStart = new Intent(MainActivity.this, ItemDetails.class);
        intentStart.putExtra("DETAILS", itemToSee);
        startActivity(intentStart);
    }
    public void enableDeleteItem(final int adapterPosition) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setMessage(R.string.are_u_sure_delete_item);
        adb.setTitle(R.string.delete);
        adb.setCancelable(false);
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                itemsAdapter.removeItem(adapterPosition);
            }
        });
        adb.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                itemsAdapter.notifyDataSetChanged();
                return;
            }
        });
        adb.show();
    }

    private void showCreateCategoryActivity(){
        Intent intentStart=new Intent(MainActivity.this, CreateCategoryActivity.class);
        startActivityForResult(intentStart, REQUEST_NEW_CATEGORY);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intentStart=new Intent(MainActivity.this, Category_Activity.class);
        startActivity(intentStart);
        return true;
    }
    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.items);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(getString(R.string.items));
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }
    private void showSnackBarMessage(String message) {
        Snackbar.make(layoutContent, message, Snackbar.LENGTH_LONG).show();
    }
}

