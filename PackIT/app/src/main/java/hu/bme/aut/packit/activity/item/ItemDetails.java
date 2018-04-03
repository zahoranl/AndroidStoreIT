package hu.bme.aut.packit.activity.item;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import hu.bme.aut.packit.R;
import hu.bme.aut.packit.data.Item;

public class ItemDetails extends AppCompatActivity {
    private TextView tvItemNev;
    private TextView tvCatNev;
    private TextView tvMegj;
    private TextView tvPlace;
    private TextView tvQR;
    private TextView tvDb;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        intent = getIntent();
        Item item = (Item) intent.getSerializableExtra("DETAILS");
        intent.getStringExtra("DETAILS");

        if (item != null) {
            tvItemNev = (TextView) findViewById(R.id.tvItemNameDet);
            tvCatNev = (TextView) findViewById(R.id.tvCatNameDet);
            tvMegj = (TextView) findViewById(R.id.tvMegjDet);
            tvPlace = (TextView) findViewById(R.id.tvPlaceDet);
            tvQR = (TextView) findViewById(R.id.tvQRDet);
            tvDb = (TextView) findViewById(R.id.tvNumberDet);

            tvItemNev.setText(item.getItemName());
            tvCatNev.setText(item.getCategoryName());
            tvMegj.setText(item.getMegjegyzes());
            tvPlace.setText(item.getElhelyezes());
            tvQR.setText(item.getCode());
            tvDb.setText(Integer.toString(item.getDarab()));
        }
    }

}
