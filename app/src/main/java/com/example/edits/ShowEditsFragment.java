package com.example.edits;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class ShowEditsFragment extends Fragment implements EditAdapter.OnEditActionListener {

    private RecyclerView rvEdits;
    private TextView tvEmpty;
    private ArrayList<Edit> editList;
    private EditAdapter adapter;
    private FirebaseServices fbs;

    public ShowEditsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_edits, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fbs = FirebaseServices.getInstance();
        rvEdits = view.findViewById(R.id.rvEdits);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        editList = new ArrayList<>();
        adapter = new EditAdapter(requireContext(), editList, this);

        rvEdits.setLayoutManager(new LinearLayoutManager(getContext()));
        rvEdits.setAdapter(adapter);

        loadEdits();
    }

    // Read every document in 'edits' collection from Firestore
    private void loadEdits() {
        fbs.getDb().collection("edits").get()
                .addOnSuccessListener(result -> {
                    if (!isAdded()) return; // Prevent crash if user left screen
                    editList.clear();
                    for (DocumentSnapshot document : result.getDocuments()) {
                        Edit edit = document.toObject(Edit.class);
                        if (edit != null) {
                            edit.setId(document.getId()); // Ensure unique ID is set
                            editList.add(edit);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    tvEmpty.setVisibility(editList.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> showMessage("Could not load: " + e.getMessage()));
    }

    @Override
    public void onUpdateClick(Edit edit) {
        EditDialog dialog = EditDialog.newInstance(edit);
        dialog.setOnSavedListener(this::loadEdits); // Refresh the list after saving
        dialog.show(getChildFragmentManager(), "edit_dialog");
    }

    @Override
    public void onDeleteClick(Edit edit) {
        // Confirmation Dialog before deleting
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Edit")
                .setMessage("Are you sure you want to delete \"" + edit.getTitle() + "\"?")
                .setPositiveButton("Yes", (dialog, which) -> deleteEditFromFirestore(edit))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteEditFromFirestore(Edit edit) {
        fbs.getDb().collection("edits").document(edit.getId())
                .delete()
                .addOnSuccessListener(unused -> {
                    showMessage("Edit deleted");
                    loadEdits(); // Refresh list
                })
                .addOnFailureListener(e -> showMessage("Delete failed: " + e.getMessage()));
    }

    private void showMessage(String message) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}