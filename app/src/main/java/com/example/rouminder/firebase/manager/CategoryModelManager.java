package com.example.rouminder.firebase.manager;

import static com.example.rouminder.firebase.manager.BaseModelManager.checkUidInitialized;

import androidx.annotation.NonNull;

import com.example.rouminder.firebase.model.CategoryModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoryModelManager {
    private static CategoryModelManager instance = new CategoryModelManager();

    private final BaseModelManager baseModelManager = BaseModelManager.getInstance();
    private final DatabaseReference ref;
    private ArrayList<CategoryModel> categories;
    private final String uid;

    private CategoryModelManager() {
        FirebaseDatabase db = baseModelManager.db;
        uid = baseModelManager.uid;
        ref = db.getReference("category");
        categories = new ArrayList<>();
    }

    public static CategoryModelManager getInstance() {
        return instance;
    }

    public CategoryModel createCategory(String categoryName) {
        checkUidInitialized();

        String created_at = baseModelManager.getTimeStampString();
        String randomId = baseModelManager.getRandomId();

        ref.child("data").child(randomId).child("author").setValue(uid);
        ref.child("data").child(randomId).child("name").setValue(categoryName);
        ref.child("data").child(randomId).child("created_at").setValue(created_at);
        ref.child("data").child(randomId).child("modified_at").setValue("");

        CategoryModel newCategory = new CategoryModel(randomId, uid, created_at, "", categoryName);

        return newCategory;
    }

    public void syncConditionModels() {
        checkUidInitialized();

        Query select = ref.child("data");
        select.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, HashMap<String, String>> result =
                        (HashMap<String, HashMap<String, String>>) dataSnapshot.getValue();

                categories = new ArrayList<>();
                for (String id : result.keySet()) {
                    HashMap<String, String> data = result.get(id);
                    String author = data.get("author");

                    if (author.equals(uid)) {
                        categories.add(new CategoryModel(
                                id, uid, data.get("created_at"), data.get("modified_at"),
                                data.get("name")));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException();
            }
        });
    }

    public ArrayList<CategoryModel> getCategoryModels() {
        return categories;
    }
}
