package com.example.edits;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.StorageReference;

public class AddEditFragment extends Fragment {

    private EditText etTitle, etShowName, etCharacterName, etEmbedUrl;
    private ImageView ivThumbnail;
    private Button btnAdd;

    private Uri selectedImageUri; // The image/thumbnail the user picked (null until they pick one)
    private FirebaseServices fbs;

    // Opens the gallery. When the user picks an image, we get its Uri here.
    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivThumbnail.setImageURI(uri);
                }
            }
    );

    public AddEditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fbs = FirebaseServices.getInstance();

        etTitle = view.findViewById(R.id.etEditTitle);
        etShowName = view.findViewById(R.id.etShowName);
        etCharacterName = view.findViewById(R.id.etCharacterName);
        etEmbedUrl = view.findViewById(R.id.etEmbedUrl);
        ivThumbnail = view.findViewById(R.id.ivThumbnail);
        btnAdd = view.findViewById(R.id.btnSaveEdit);

        ivThumbnail.setOnClickListener(v -> pickImage.launch("image/*"));
        btnAdd.setOnClickListener(v -> addEdit());
    }

    private void addEdit() {
        String title = etTitle.getText().toString().trim();
        String showName = etShowName.getText().toString().trim();
        String characterName = etCharacterName.getText().toString().trim();
        String embedUrl = etEmbedUrl.getText().toString().trim();

        // 1. Check the form before we talk to Firebase
        if (title.isEmpty() || showName.isEmpty() || embedUrl.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in title, show name and embed URL", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri == null) {
            Toast.makeText(getContext(), "Please pick a thumbnail image", Toast.LENGTH_SHORT).show();
            return;
        }

        btnAdd.setEnabled(false); // Stop double clicks while we upload

        // 2. A new, empty document gives us a unique ID for the edit
        DocumentReference doc = fbs.getDb().collection("edits").document();
        StorageReference imageRef = fbs.getStorage().getReference()
                .child("thumbnails/" + doc.getId() + ".jpg");

        // Step 1: Upload the image to Storage
        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(snapshot -> {
                    // Step 2: Ask Storage for the image's download URL
                    imageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> saveEditToFirestore(doc, title, showName, characterName, embedUrl, uri.toString()))
                            .addOnFailureListener(this::showError);
                })
                .addOnFailureListener(this::showError);
    }

    // Step 3: Create the Edit object and save it in Firestore
    private void saveEditToFirestore(DocumentReference doc, String title, String showName,
                                     String characterName, String embedUrl, String imageUrl) {

        // Using a basic constructor or setters with your Edit model
        Edit edit = new Edit();
        edit.setId(doc.getId());
        edit.setTitle(title);
        edit.setShowName(showName);
        edit.setCharacterName(characterName);
        edit.setEmbedUrl(embedUrl);
        edit.setImageUrl(imageUrl);

        doc.set(edit)
                .addOnSuccessListener(unused -> {
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), "Edit added successfully!", Toast.LENGTH_SHORT).show();
                    }
                    if (btnAdd != null) {
                        btnAdd.setEnabled(true);
                    }
                })
                .addOnFailureListener(this::showError);
    }

    private void showError(Exception e) {
        if (btnAdd != null) {
            btnAdd.setEnabled(true);
        }
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}