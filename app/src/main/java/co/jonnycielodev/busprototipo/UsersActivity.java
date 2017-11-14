package co.jonnycielodev.busprototipo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import co.jonnycielodev.busprototipo.entities.Users;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mTextView;

    private ValueEventListener mEventListener;
    private DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Users");
    private ArrayList<Users> mUsers = new ArrayList<>();
    private UsersAdapter adapter;

    @Override
    protected void onStart() {
        super.onStart();
        if (mEventListener != null){
            mReference.addValueEventListener(mEventListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mEventListener != null){
            mReference.removeEventListener(mEventListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        Toolbar toolbar = findViewById(R.id.userToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Lista de usuarios");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mProgressBar = findViewById(R.id.userProgressBar);
        mRecyclerView = findViewById(R.id.userRecyclerView);
        mTextView = findViewById(R.id.userTextView);

        setupAdapter();
        setupRecuclerView();

        mEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    Users user = data.getValue(Users.class);

                    mUsers.add(user);
                }

                adapter.notifyDataSetChanged();

                if (mUsers.isEmpty()){
                    mProgressBar.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);
                    mTextView.setVisibility(View.VISIBLE);
                }else {
                    mProgressBar.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mReference.addValueEventListener(mEventListener);
        mReference.keepSynced(true);
    }

    private void setupRecuclerView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(adapter);
    }

    private void setupAdapter() {
        adapter = new UsersAdapter(this, mUsers);
    }
}
