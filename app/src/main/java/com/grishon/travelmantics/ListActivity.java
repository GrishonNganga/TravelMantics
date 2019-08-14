package com.grishon.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class ListActivity extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        FirebaseUtil.openFbReference("traveldeals", this);
        auth = FirebaseUtil.firebaseAuth;
        user = auth.getCurrentUser();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_deal, menu);
        MenuItem insertMenu = menu.findItem(R.id.add_deal);
        if (FirebaseUtil.isAdmin){
            insertMenu.setVisible(true);
        }
        else {

            insertMenu.setVisible(false);

        }


        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.dettachListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = auth.getCurrentUser();

    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.openFbReference("traveldeals", this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        final DealAdapter dealAdapter = new DealAdapter();
        recyclerView.setAdapter(dealAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        FirebaseUtil.attachListener();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_deal:
                Intent intent = new Intent(this, DealActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout_menu:
                logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Logout", "User Logged Out");
                        FirebaseUtil.attachListener();
                    }
                });
        FirebaseUtil.dettachListener();

    }
    public void showMenu(){
        invalidateOptionsMenu();
    }
}
