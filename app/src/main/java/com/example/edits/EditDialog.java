package com.example.edits;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EditDialog extends DialogFragment {

    public interface OnSavedListener {
        void onSaved();
    }

    private static final String ARG_EDIT = "edit";

    private Edit edit;
    private OnSavedListener onSavedListener;
    private EditText etTitle, etShowName, etCharacterName;
    private Button btnSave, btnCancel;

    // Use this to create the dialog; it puts the edit inside the arguments bundle
    public static EditDialog newInstance(Edit edit) {
        EditDialog dialog = new EditDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EDIT, edit);
        dialog.setArguments(args);
        return dialog;
    }

    public void setOnSavedListener(OnSavedListener listener) {
        this.onSavedListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            edit = getArguments().getSerializable(ARG_EDIT, Edit.class);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etTitle = view.findViewById(R.id.etTitle);
        etShowName = view.findViewById(R.id.etShowName);
        etCharacterName = view.findViewById(R.id.etCharacterName);
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);

        if (edit != null) {
            etTitle.setText(edit.getTitle());
            etShowName.setText(edit.getShowName());
            etCharacterName.setText(edit.getCharacterName());
        }

        btnSave.setOnClickListener(v -> saveChanges());
        btnCancel.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onStart() {
        super.onStart();
        // Make the dialog use full screen width layout nicely
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void saveChanges() {
        if (edit == null) return;

        String title = etTitle.getText().toString().trim();
        if (title.isEmpty()) {
            etTitle.setError("Title is required");
            return;
        }

        edit.setTitle(title);
        edit.setShowName(etShowName.getText().toString().trim());
        edit.setCharacterName(etCharacterName.getText().toString().trim());

        // Overwrite the document that has this edit's ID in Firestore
        FirebaseServices.getInstance().getDb()
                .collection("edits").document(edit.getId())
                .set(edit)
                .addOnSuccessListener(unused -> {
                    if (!isAdded()) return;
                    Toast.makeText(getContext(), "Changes saved", Toast.LENGTH_SHORT).show();
                    if (onSavedListener != null) {
                        onSavedListener.onSaved();
                    }
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Toast.makeText(getContext(), "Save failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}