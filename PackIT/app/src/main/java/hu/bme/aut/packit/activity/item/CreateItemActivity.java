package hu.bme.aut.packit.activity.item;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.packit.R;
import hu.bme.aut.packit.activity.MainActivity;
import hu.bme.aut.packit.activity.ScanActivity;
import hu.bme.aut.packit.data.Category;
import hu.bme.aut.packit.data.Item;

import static android.R.id.message;
import static android.os.Build.VERSION_CODES.M;
import static hu.bme.aut.packit.R.id.drawerLayout;
import static hu.bme.aut.packit.R.id.layoutContent;
import static hu.bme.aut.packit.activity.MainActivity.CAMERA;
import static hu.bme.aut.packit.activity.MainActivity.PERMISSION_REQUEST;
import static hu.bme.aut.packit.activity.MainActivity.REQUEST_CODE_QRCODE;

public class CreateItemActivity  extends AppCompatActivity {
    public static final String KEY = "KEY";
    private EditText etItemNev;
    private EditText etMegj;
    private EditText etPlace;
    private EditText etQR;
    private EditText etDb;
    public Spinner spinnerCategory;
    private Button btnSave;
    private Button btnScan1;
    private Item itemToEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);
        if (getIntent().getSerializableExtra(MainActivity.KEY_EDIT) != null) {
            itemToEdit = (Item) getIntent().getSerializableExtra(MainActivity.KEY_EDIT);
        }

        etItemNev= (EditText) findViewById(R.id.etItemName);
        etMegj=(EditText) findViewById(R.id.etMegj);
        etPlace=(EditText) findViewById(R.id.etPlace);
        etQR=(EditText) findViewById(R.id.etItemQR);
        etDb=(EditText) findViewById(R.id.etDb);
        btnSave= (Button) findViewById(R.id.btnSave);
        btnScan1= (Button) findViewById(R.id.btnScan);
        spinnerCategory= (Spinner) findViewById(R.id.spinnerCategoryType);
        fillSpinner(null);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etItemNev.getText().toString().isEmpty()) {
                    etItemNev.requestFocus();
                    etItemNev.setError(getString(R.string.item_name_needed));
                    return;
                }
                if (etDb.getText().toString().isEmpty()) {
                    etDb.setText("0");
                }
                saveItem();
            }
        });
        btnScan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ContextCompat.checkSelfPermission(CreateItemActivity.this, CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(CreateItemActivity.this, new String[]{CAMERA}, PERMISSION_REQUEST);
                }
                if(ContextCompat.checkSelfPermission(CreateItemActivity.this, CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(CreateItemActivity.this, ScanActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_QRCODE);
                }

            }
        });
        if (itemToEdit != null) {
            etItemNev.setText(itemToEdit.getItemName());
            fillSpinner(itemToEdit.getCategoryName());
            etMegj.setText(itemToEdit.getMegjegyzes());
            etPlace.setText(itemToEdit.getElhelyezes());
            etQR.setText(itemToEdit.getCode());
            etDb.setText(Integer.toString(itemToEdit.getDarab()));
        }
    }

    private void saveItem() {
        Intent intentResult = new Intent();
        Item itemResult = null;
        if (itemToEdit != null)
            itemResult  = itemToEdit;
         else itemResult  = new Item();

        itemResult.setItemName(etItemNev.getText().toString());
        if (spinnerCategory.getSelectedItem()!=null)
            itemResult.setCategoryName(spinnerCategory.getSelectedItem().toString());
        else itemResult.setCategoryName(getString(R.string.no_category));
        itemResult.setMegjegyzes(etMegj.getText().toString());
        itemResult.setElhelyezes(etPlace.getText().toString());
        itemResult.setCode(etQR.getText().toString());
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
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(CreateItemActivity.this,R.layout.support_simple_spinner_dropdown_item,spinnerArray);
        spinnerCategory.setAdapter(spinnerArrayAdapter);
        spinnerCategory.setSelection(place);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(CreateItemActivity.this, ScanActivity.class);
            startActivityForResult(intent, REQUEST_CODE_QRCODE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case REQUEST_CODE_QRCODE:
                        if (data != null) {
                            final Barcode barcode = data.getParcelableExtra("barcode");
                            etQR.setText(barcode.displayValue);
                        }
                        break;
                }
                break;
            case RESULT_CANCELED: break;
        }
    }
}
