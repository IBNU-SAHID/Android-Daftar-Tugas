package com.project.daftartugas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class TambahTugasBaru extends BottomSheetDialogFragment {
    public static final String TAG = "TambahTugasBaru";

    private TextView setDueDate;
    private EditText mTaskEdit;
    private Button mSaveBtn;
    private Context context;

    private String dueDate = "";

    private String id = "";
    private String dueDateUpdate = "";

    private FirebaseFirestore firestore;

    public static TambahTugasBaru newInstance() {
        return new TambahTugasBaru();
    }

    @androidx.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @androidx.annotation.Nullable ViewGroup container, @androidx.annotation.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tambah_tugas_baru, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setDueDate = view.findViewById(R.id.set_due_date);
        mTaskEdit = view.findViewById(R.id.task_edittext);

        mSaveBtn = view.findViewById(R.id.save_btn);

        firestore = FirebaseFirestore.getInstance();

        boolean isUpdate = false;

        final Bundle bundle = getArguments();

        if (bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            id = bundle.getString("id");
            dueDateUpdate = bundle.getString("due");

            mTaskEdit.setText(task);
            setDueDate.setText(dueDateUpdate);

            if (task.length() > 0){
                mSaveBtn.setEnabled(false);
            }

        }

        mTaskEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    mSaveBtn.setEnabled(false);
                    mSaveBtn.setTextColor(Color.GRAY);
                } else {
                    mSaveBtn.setEnabled(true);
                    mSaveBtn.setTextColor(getResources().getColor(R.color.holo_orange_light));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setDueDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                int MONTH = calendar.get(Calendar.MONTH);
                int YEAR = calendar.get(Calendar.YEAR);
                int DAY = calendar.get(Calendar.DATE);


                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        setDueDate.setText(dayOfMonth + "/" + month + "/" + year);
                        dueDate =dayOfMonth + "/" + month +"/"+year;

                    }
                } , YEAR , MONTH , DAY);

                datePickerDialog.show();
            }
        });

        boolean finalIsUpdate = isUpdate;
        mSaveBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String task = mTaskEdit.getText().toString();

                if (finalIsUpdate){
                    firestore.collection("task").document(id).update("task",task, "due",dueDate);
                    Toast.makeText(context,"Tugas berhasil di Perbarui",Toast.LENGTH_SHORT).show();

                }else {

                    if (task.isEmpty()) {
                        Toast.makeText(context, "Task tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> taskMap = new HashMap<>();

                        taskMap.put("task", task);
                        taskMap.put("due", dueDate);
                        taskMap.put("status", 0);
                        taskMap.put("time", FieldValue.serverTimestamp());


                        firestore.collection("task").add(taskMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Tugas Disimpan", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                dismiss();
            }
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        Activity activity = getActivity();
        if (activity instanceof OnDialogCloseListener){
            ((OnDialogCloseListener) activity).onDialogClose(dialog);
        }
    }
}
