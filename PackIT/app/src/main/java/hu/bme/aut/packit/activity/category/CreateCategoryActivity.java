package hu.bme.aut.packit.activity.category;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import hu.bme.aut.packit.R;
import hu.bme.aut.packit.activity.MainActivity;
import hu.bme.aut.packit.adapter.CategoriesAdapter;
import hu.bme.aut.packit.data.Category;
import hu.bme.aut.packit.data.Item;

public class CreateCategoryActivity extends AppCompatActivity {
    public static final String KEY = "KEY";

    private EditText etCatNev;
    private EditText etMegj;

    private Category categoryToEdit = null;
    private CategoriesAdapter categoriesAdapter;
    private List<Category> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);
        categoryList= Category.listAll(Category.class);
        categoriesAdapter = new CategoriesAdapter(categoryList, this);

        if (getIntent().getSerializableExtra(MainActivity.KEY_EDIT) != null) {
            categoryToEdit = (Category) getIntent().getSerializableExtra(MainActivity.KEY_EDIT);
        }

        etCatNev = (EditText) findViewById(R.id.etCatName);
        etMegj = (EditText) findViewById(R.id.etMegj);

        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etCatNev.getText().toString().isEmpty()) {
                    etCatNev.requestFocus();
                    etCatNev.setError(getString(R.string.item_name_needed));
                    return;
                }
                savePlace();
            }
        });

        if (categoryToEdit != null) {
            etCatNev.setText(categoryToEdit.getCategoryName());
            etMegj.setText(categoryToEdit.getMegjegyzes());
        }
    }

    private void savePlace() {
        Intent intentResult = new Intent();
        Category categoryResult = null;
        if (categoryToEdit != null) {
            categoryResult  = categoryToEdit;
        } else {
            categoryResult  = new Category();
        }
        categoryResult.setCategoryName(etCatNev.getText().toString());
        categoryResult.setMegjegyzes(etMegj.getText().toString());
        intentResult.putExtra(KEY, categoryResult);
        setResult(RESULT_OK, intentResult);
        finish();
    }
}

