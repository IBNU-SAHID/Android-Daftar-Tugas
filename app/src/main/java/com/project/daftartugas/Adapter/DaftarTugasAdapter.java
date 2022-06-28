package com.project.daftartugas.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.project.daftartugas.TambahTugasBaru;
import com.project.daftartugas.MainActivity;
import com.project.daftartugas.Model.DaftarTugasModel;
import com.project.daftartugas.R;

import java.util.List;

public class DaftarTugasAdapter extends RecyclerView.Adapter<DaftarTugasAdapter.MyViewHolder> {

    private final List<DaftarTugasModel> todoList;
    private final MainActivity activity;
    private FirebaseFirestore firestore;

    public DaftarTugasAdapter(MainActivity mainActivity, List<DaftarTugasModel> todoList) {
        this.activity = mainActivity;
        this.todoList = todoList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.each_task , parent, false);
        firestore = FirebaseFirestore.getInstance();

        return new MyViewHolder(view);
    }

    public void deleteTask(int position){
        DaftarTugasModel daftarTugasModel = todoList.get(position);
        firestore.collection("task").document(daftarTugasModel.TaskId).delete();
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public Context getContext (){
        return activity;
    }
    public void editTask(int position){
        DaftarTugasModel daftarTugasModel = todoList.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("task", daftarTugasModel.getTask());
        bundle.putString("due", daftarTugasModel.getDue());
        bundle.putString("id", daftarTugasModel.TaskId);

        TambahTugasBaru tambahTugasBaru = new TambahTugasBaru();
        tambahTugasBaru.setArguments(bundle);
        tambahTugasBaru.show(activity.getSupportFragmentManager() , tambahTugasBaru.getTag());

    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        DaftarTugasModel daftarTugasModel = todoList.get(position);
        holder.mCheckBox.setText(daftarTugasModel.getTask());

        holder.mDueDateTv.setText("Dikumpulkan pada " + daftarTugasModel.getDue());

        holder.mCheckBox.setChecked(toBoolean(daftarTugasModel.getStatus()));

        holder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                firestore.collection("task").document(daftarTugasModel.TaskId).update("status" , 1);

            }else{
                firestore.collection("task").document(daftarTugasModel.TaskId).update("status" , 0);
            }
        });

        holder.mBtnEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editTask(position);
            }
        });


    }

    private boolean toBoolean(int status) {
        return status != 0;
    }

    @Override
    public int getItemCount() {
        return todoList.size();

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mDueDateTv;
        CheckBox mCheckBox;
        Button mBtnEdit;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mDueDateTv = itemView.findViewById(R.id.due_date_tv);
            mCheckBox = itemView.findViewById(R.id.mcheckbox);
            mBtnEdit = itemView.findViewById(R.id.btn_edit);

        }
    }
}
