package com.project.daftartugas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.daftartugas.Adapter.DaftarTugasAdapter;
import com.project.daftartugas.Model.DaftarTugasModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity implements OnDialogCloseListener {
    private RecyclerView recyclerView;
    private FloatingActionButton mFab;
    private FirebaseFirestore firestore;
    private DaftarTugasAdapter adapter;
    private List<DaftarTugasModel> mList;
    private Query query;
    private ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        mFab = findViewById(R.id.floatingActionButton);
        firestore = FirebaseFirestore.getInstance();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TambahTugasBaru.newInstance().show(getSupportFragmentManager(), TambahTugasBaru.TAG);

            }
        });

        mList = new ArrayList<>();
        adapter = new DaftarTugasAdapter(MainActivity.this, mList);

        recyclerView.setAdapter(adapter);
        showData();

    }

    private void showData(){
        query = firestore.collection("task").orderBy("time", Query.Direction.DESCENDING);

        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED){

                        String id = documentChange.getDocument().getId();
                        DaftarTugasModel daftarTugasModel = documentChange.getDocument().toObject(DaftarTugasModel.class).withId(id);

                        mList.add(daftarTugasModel);
                        adapter.notifyDataSetChanged();

                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(adapter));
                        itemTouchHelper.attachToRecyclerView(recyclerView);

                        Collections.reverse(mList);
                    }
                }
                listenerRegistration.remove();

            }
        });
    }

    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        mList.clear();
        showData();
        adapter.notifyDataSetChanged();
    }
}