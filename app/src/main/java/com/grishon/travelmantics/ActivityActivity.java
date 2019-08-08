package com.grishon.travelmantics;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityActivity extends AppCompatActivity {
    EditText titleText, priceText, descriptionText;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    Button imageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity);

        titleText = findViewById(R.id.titleText);
        priceText = findViewById(R.id.priceText);
        descriptionText = findViewById(R.id.descriptionText);
        imageBtn = findViewById(R.id.btnImage);

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = titleText.getText().toString();
                String price = priceText.getText().toString();
                String description = descriptionText.getText().toString();

                TravelDeal travelDeal = new TravelDeal(title, price, description, "");
                reference.push().setValue(travelDeal);
                clean();
            }
        });
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference().child("traveldeals");
    }
    private void clean() {
        titleText.setText("");
        priceText.setText("");
        descriptionText.setText("");
        titleText.requestFocus();
    }
}
