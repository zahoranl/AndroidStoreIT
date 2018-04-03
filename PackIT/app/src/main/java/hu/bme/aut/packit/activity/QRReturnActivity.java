package hu.bme.aut.packit.activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.android.gms.vision.barcode.Barcode;
import java.util.ArrayList;
import java.util.List;


import hu.bme.aut.packit.R;
import hu.bme.aut.packit.activity.item.CreateItemActivity;
import hu.bme.aut.packit.adapter.ItemsAdapter;
import hu.bme.aut.packit.data.Category;
import hu.bme.aut.packit.data.Item;

import static hu.bme.aut.packit.activity.category.CreateCategoryActivity.KEY;


public class QRReturnActivity extends AppCompatActivity {
    private LayoutInflater inflater;
    private LinearLayout qrreturn;
    private Intent intent;

    public LinearLayout AddLayout;
    public Button btnAddItemScan;
    public Button btnReScan;
    public TextView result;

    private EditText etItemNev;
    private EditText etMegj;
    private EditText etPlace;
    private EditText etQR;
    private EditText etDb;
    public Spinner spinnerCategory;
    private Button btnSave;
    private Button btnScan1;

    private TextView tvItemNev;
    private TextView tvCatNev;
    private TextView tvMegj;
    private TextView tvPlace;
    private TextView tvQR;
    private TextView tvDb;

    private Item itemToEditHolder;
    private int itemToEditPosition;
    private ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrreturn);
        intent = getIntent();

        btnReScan = (Button) findViewById(R.id.btnReScan);
        btnAddItemScan = (Button) findViewById(R.id.btnAddItemScan);
        qrreturn = (LinearLayout) findViewById(R.id.contentLayoutScan);
        result = (TextView) findViewById(R.id.result);
        result.setText(intent.getStringExtra("QRCODE"));
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        find();

        btnAddItemScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddLayout = (LinearLayout) inflater.inflate(R.layout.activity_create_item, null);
                qrreturn.addView(AddLayout);
                btnAddItemScan.setVisibility(View.GONE);

                etItemNev= (EditText) findViewById(R.id.etItemName);
                etMegj=(EditText) findViewById(R.id.etMegj);
                etPlace=(EditText) findViewById(R.id.etPlace);
                etQR=(EditText) findViewById(R.id.etItemQR);
                etDb=(EditText) findViewById(R.id.etDb);
                spinnerCategory= (Spinner) findViewById(R.id.spinnerCategoryType);
                fillSpinner(null);

                btnSave= (Button) findViewById(R.id.btnSave);
                btnScan1= (Button) findViewById(R.id.btnScan);

                btnScan1.setEnabled(false);
                etQR.setText(result.getText());
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveItem();
                    }
                });
            }
        });

        btnReScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(QRReturnActivity.this, MainActivity.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(QRReturnActivity.this, new String[]{MainActivity.CAMERA}, MainActivity.PERMISSION_REQUEST);
                }
                Intent intent = new Intent(QRReturnActivity.this, ScanActivity.class);
                startActivityForResult(intent, MainActivity.REQUEST_CODE_QRCODE);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case MainActivity.REQUEST_CODE_QRCODE:
                        final Barcode barcode = data.getParcelableExtra("barcode");
                        result.setText(barcode.displayValue);
                        find();
                        break;
                    case MainActivity.REQUEST_EDIT_ITEM:
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
                }
        }
    }

    private void saveItem() {
        Intent intentResult = new Intent();
        Item itemResult  = new Item();
        itemResult.setItemName(etItemNev.getText().toString());
        if (spinnerCategory.getSelectedItem()!=null)
            itemResult.setCategoryName(spinnerCategory.getSelectedItem().toString());
        else itemResult.setCategoryName(getString(R.string.no_category));
        itemResult.setMegjegyzes(etMegj.getText().toString());
        itemResult.setElhelyezes(etPlace.getText().toString());
        itemResult.setCode(etQR.getText().toString());
        if (etDb.getText().toString().isEmpty()) etDb.setText("0");
        itemResult.setDarab(Integer.parseInt(etDb.getText().toString()));
        intentResult.putExtra(KEY, itemResult);
        setResult(RESULT_OK, intentResult);
        finish();
    }

    private void fillSpinner(String cat){
        int place=-1;
        List<Category> categoryList= Category.listAll(Category.class);
        ArrayList<String> spinnerArray = new ArrayList<String>();
        for (int i=0;i<categoryList.size();i++){
            if (cat!=null && cat.equals(categoryList.get(i).getCategoryName())) place=i;
            spinnerArray.add(categoryList.get(i).getCategoryName());
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(QRReturnActivity.this,R.layout.support_simple_spinner_dropdown_item,spinnerArray);
        spinnerCategory.setAdapter(spinnerArrayAdapter);
        spinnerCategory.setSelection(place);
    }
    private Item searchItem(String code) {
        List<Item> itemList = Item.listAll(Item.class);
        for(int i=0;i<itemList.size();i++){
            if (itemList.get(i).getCode().equals(code))
                return itemList.get(i);
        }
        return null;
    }
    private void find(){
        final Item find=searchItem(result.getText().toString());
        if (find!=null){
            result.setText(find.getItemName());
            btnAddItemScan.setVisibility(View.GONE);
            AddLayout = (LinearLayout) inflater.inflate(R.layout.activity_item_details, null);
            qrreturn.removeAllViews();
            qrreturn.addView(AddLayout);
            tvItemNev = (TextView) findViewById(R.id.tvItemNameDet);
            tvCatNev = (TextView)findViewById(R.id.tvCatNameDet);
            tvMegj = (TextView)findViewById(R.id.tvMegjDet);
            tvPlace = (TextView)findViewById(R.id.tvPlaceDet);
            tvQR = (TextView)findViewById(R.id.tvQRDet);
            tvDb = (TextView)findViewById(R.id.tvNumberDet);

            tvItemNev.setText(find.getItemName());
            tvCatNev .setText(find.getCategoryName());
            tvMegj .setText(find.getMegjegyzes());
            tvPlace .setText(find.getElhelyezes());
            tvQR .setText(find.getCode());
            tvDb.setText(Integer.toString(find.getDarab()));
        }else{
            qrreturn.removeAllViews();
            btnAddItemScan.setVisibility(View.VISIBLE);
        }
    }
}
