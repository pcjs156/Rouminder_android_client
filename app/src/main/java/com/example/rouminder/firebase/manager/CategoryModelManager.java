package com.example.rouminder.firebase.manager;

import static com.example.rouminder.firebase.manager.BaseModelManager.checkUidInitialized;
import static com.example.rouminder.firebase.manager.BaseModelManager.readData;

import androidx.annotation.NonNull;

import com.example.rouminder.firebase.model.CategoryModel;
import com.example.rouminder.firebase.exception.ModelDoesNotExists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoryModelManager {
    private static CategoryModelManager instance = new CategoryModelManager();

    private final BaseModelManager baseModelManager = BaseModelManager.getInstance();
    public final DatabaseReference ref;
    public final DatabaseReference dataRef;
    public ArrayList<CategoryModel> categories;
    private final String uid;

    private CategoryModelManager() {
        FirebaseDatabase db = baseModelManager.db;
        uid = baseModelManager.uid;
        ref = db.getReference("category");
        dataRef = ref.child("data");
        categories = new ArrayList<>();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, HashMap<String, String>> result =
                        (HashMap<String, HashMap<String, String>>) dataSnapshot.getValue();

                categories = new ArrayList<>();
                int idx = 0;
                for (String id : result.get("data").keySet()) {
                    String author = dataRef.child(uid).getKey();

                    if (author.equals(uid)) {
//                        System.out.println("FB_DB: " + dataRef.child(uid).child("name"));
                        categories.add(new CategoryModel(
                                id, uid, dataRef.child(uid).child("created_at").getKey(),
                                dataRef.child(uid).child("modified_at").getKey(), dataRef.child(uid).child("name").getKey()));
                    }
                }
                setCategories(categories);
                idx++;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static CategoryModelManager getInstance() {
        return instance;
    }

    public CategoryModel create(String categoryName) {
        checkUidInitialized();

        String created_at = baseModelManager.getTimeStampString();
        String randomId = baseModelManager.getRandomId();

        ref.child("data").child(randomId).child("author").setValue(uid);
        ref.child("data").child(randomId).child("name").setValue(categoryName);
        ref.child("data").child(randomId).child("created_at").setValue(created_at);
        ref.child("data").child(randomId).child("modified_at").setValue("");

        CategoryModel newCategory = new CategoryModel(randomId, uid, created_at, "", categoryName);
        sync();

        return newCategory;
    }

    public void sync() {
        checkUidInitialized();

        Query select = ref.child("data");
        readData(select, new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
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
            public void onStart() {

            }

            @Override
            public void onFailure() {
                System.out.println("FB_DB: CategoryModelManager.onFailure");
            }
        });
    }

    public ArrayList<CategoryModel> get() {
        checkUidInitialized();
        sync();
        return categories;
    }

    public CategoryModel get(String id) throws ModelDoesNotExists {
        checkUidInitialized();
        sync();

        CategoryModel ret = null;
        for (CategoryModel model : categories) {
            if (model.id.equals(id)) {
                ret = model;
                break;
            }
        }

        if (ret == null)
            throw new ModelDoesNotExists();
        else
            return ret;
    }

    public void setCategories(ArrayList<CategoryModel> categories) {
        this.categories = categories;
    }
}
