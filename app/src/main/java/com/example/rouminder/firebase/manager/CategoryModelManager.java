package com.example.rouminder.firebase.manager;

import static com.example.rouminder.firebase.manager.BaseModelManager.checkUidInitialized;
import static com.example.rouminder.firebase.manager.BaseModelManager.readData;

import android.widget.ArrayAdapter;

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
import java.util.HashSet;

public class CategoryModelManager {
    private static CategoryModelManager instance = new CategoryModelManager();

    private final BaseModelManager baseModelManager = BaseModelManager.getInstance();
    public final DatabaseReference ref;
    public final DatabaseReference dataRef;
    public ArrayList<CategoryModel> categories;
    private final String uid;

    private final HashSet<ArrayAdapter> notifyAdapters = new HashSet<>();

    private CategoryModelManager() {
        FirebaseDatabase db = baseModelManager.db;
        uid = baseModelManager.uid;
        ref = db.getReference("category");
        dataRef = ref.child("data");
        categories = new ArrayList<>();

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, HashMap<String, String>> result =
                        (HashMap<String, HashMap<String, String>>) dataSnapshot.getValue();

                categories.clear();
                for (String id : result.keySet()) {
                    HashMap<String, String> category = result.get(id);

                    String author = category.get("author");
                    if (author.equals(uid)) {
                        categories.add(new CategoryModel(
                                id, uid, category.get("created_at"),
                                category.get("modified_at"), category.get("name")));
                    }
                }

                for (ArrayAdapter adapter : notifyAdapters) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("REALTIME_DB: 카테고리 연동 실패");
            }
        });
    }

    public static CategoryModelManager getInstance() {
        return instance;
    }

    public ArrayList<CategoryModel> getCategories() {
        return (ArrayList<CategoryModel>) categories.clone();
    }

    public boolean addNotifyAdapter(ArrayAdapter newAdapter) {
        if (notifyAdapters.contains(newAdapter))
            return false;
        else {
            notifyAdapters.add(newAdapter);
            return true;
        }
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

        return newCategory;
    }

    public ArrayList<CategoryModel> get() {
        checkUidInitialized();
        return categories;
    }

    public CategoryModel get(String id) throws ModelDoesNotExists {
        checkUidInitialized();

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
}
