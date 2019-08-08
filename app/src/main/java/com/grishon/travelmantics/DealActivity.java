package com.grishon.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class DealActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    EditText titleText, priceText, descriptionText;
    ImageView imageView;
    public TravelDeal deal;
    private static final int PICTURE_RESULT = 42;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals);
        firebaseDatabase = FirebaseUtil.firebaseDatabase;
        databaseReference = FirebaseUtil.databaseReference;
        titleText = findViewById(R.id.titleText);
        priceText = findViewById(R.id.priceText);
        descriptionText = findViewById(R.id.descriptionText);
        imageView = findViewById(R.id.image);

        loadExistingDeal();

        Button imageBtn = findViewById(R.id.btnImage);
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                intent1.setType("image/jpeg");
                intent1.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent1.createChooser(intent1, "Insert Picture"), PICTURE_RESULT);
            }
        });
    }

    private void loadExistingDeal() {
        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
        if (deal == null) {
            deal = new TravelDeal();
        }
        this.deal = deal;
        titleText.setText(deal.getTitle());
        priceText.setText(deal.getPrice());
        descriptionText.setText(deal.getDescription());
        showImage(deal.getImgUrl());
    }


    private void clean() {
        titleText.setText("");
        priceText.setText("");
        descriptionText.setText("");
        titleText.requestFocus();
    }

    private void saveDeal() {
        deal.setTitle(titleText.getText().toString());
        deal.setDescription(descriptionText.getText().toString());
        deal.setPrice(priceText.getText().toString());
        deal.setImgUrl(deal.getImgUrl());
        if (deal.getId() == null) {
            databaseReference.push().setValue(deal);
        } else
            editDeal();
    }

    private void deleteDeal() {
        if (deal == null) {
            Toast.makeText(this, "Error! Please save Deal before deleting", Toast.LENGTH_LONG).show();
            return;
        }
        databaseReference.child(deal.getId()).removeValue();
        finish();
    }

    private void editDeal() {

        databaseReference.child(deal.getId()).setValue(deal);

}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        if (FirebaseUtil.isAdmin) {
            menu.findItem(R.id.remove_menu).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            enableEditTexts(true);
        } else {
            menu.findItem(R.id.remove_menu).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableEditTexts(false);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        imageUri = data.getData();
        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
            final StorageReference ref = FirebaseUtil.storageReference.child("deal_pics")
                    .child((Objects.requireNonNull(imageUri.getLastPathSegment())));
            ref.putFile(imageUri).addOnCompleteListener(this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                    String imageName = task.getResult().getStorage().getPath();
                    Log.d("imageName", imageName);

                    deal.setImgUrl(imageName);
                    if (task.isSuccessful()) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();
                                deal.setImgUrl(downloadUrl);
                                showImage(deal.getImgUrl());
                                //Toast.makeText(DealActivity.this, "Deal Saved", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("PushError", e.getMessage());
                            }
                        });
                    }
                }
            });
        }
    }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            switch (item.getItemId()) {
                case R.id.save_menu:
                    saveDeal();
                    Toast.makeText(this, "Deal Saved", Toast.LENGTH_LONG).show();
                    clean();
                    backToList();
                    return true;
                case R.id.remove_menu:
                    deleteDeal();
                    Toast.makeText(this, "Deleted deal successfully", Toast.LENGTH_LONG).show();

                    clean();
                    backToList();
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

        private void backToList () {
            Intent intent = new Intent(this, ListActivity.class);
            startActivity(intent);
        }
        public void enableEditTexts ( boolean isEnabled){

        titleText.setEnabled(isEnabled);
        priceText.setEnabled(isEnabled);
        descriptionText.setEnabled(isEnabled);

        }
        private void showImage(String url){
            if (url != null && url.isEmpty() == false) {
                int width = Resources.getSystem().getDisplayMetrics().widthPixels;
                Picasso.get()
                        .load(url)
                        .resize(width, width*2/3)
                        .centerCrop()
                        .into(imageView);
            }
        }
    }
