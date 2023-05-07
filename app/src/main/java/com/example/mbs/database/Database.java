package com.example.mbs.database;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mbs.screens.Router;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class Database {
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    public Database() {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    public void emailPasswordAuthentication(String email, String password, Context context) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Giriş yapıldı.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, Router.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "Email ya da şifre doğru değil.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void registerNewUser(Uri file, String email, String password, String fullName,
                                String startdate, String graduationDate, Context context) {
        try {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task_auth -> {
                        if (task_auth.isSuccessful()) {
                            saveUserInfos(file, email, fullName, startdate,
                                    graduationDate, context);
                        } else if (password.length() < 6){
                            Toast.makeText(context, "Şifre uzunluğu 6 karakterden düşük olamaz.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Bu mail ile kayıt zaten oluşturulmuş.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e){
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void saveUserInfos(Uri file, String email, String fullName,
                              String birthdate, String graduationDate, Context context) {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        StorageReference profileRef = storageRef.child("users/" + userId + "/profile-photo.jpg");

        UploadTask uploadTask = profileRef.putFile(file);
        uploadTask.addOnCompleteListener(task_storage -> {
            if (task_storage.isSuccessful()){
                User newUser = new User(profileRef.getPath(), userId, email, fullName, birthdate, graduationDate);
                mFirestore.collection("users")
                        .document(userId)
                        .set(newUser)
                        .addOnCompleteListener(task_firestore -> {
                            if (task_firestore.isSuccessful()) {
                                Toast.makeText(context, "Kullanıcı başarıyla kaydedildi.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Kullanıcı kaydedilemedi.",
                                        Toast.LENGTH_SHORT).show();
                                Log.e("TAG", "Hata: ", task_firestore.getException());
                                user.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(Task<Void> task_delete_auth) {
                                                if (task_delete_auth.isSuccessful()) {
                                                    Log.d("TAG", "User account deleted.");
                                                }
                                            }
                                        });

                            }
                        });
            } else {
                Toast.makeText(context, "Kullanıcı kaydedilemedi.", Toast.LENGTH_SHORT).show();
                Log.e("TAG", "Hata: ", task_storage.getException());
                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task_delete_auth) {
                                if (task_delete_auth.isSuccessful()) {
                                    Log.d("TAG", "User account deleted.");
                                }
                            }
                        });
            }
        });
    }

    public void changePassword(String email, Context context){
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Check Your Email",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void addAnnouncement(Announcements announcements){
        announcements.type = Announcements.TEXT_TYPE;
        mFirestore.collection("announcements")
                .document(announcements.getId())
                .set(announcements)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful())
                        Log.e("TAG", "Hata: ", task.getException());
                });
    }
    public void addAnnouncement(Announcements announcements, Uri file){
        announcements.type = Announcements.IMAGE_TYPE;
        StorageReference profileRef = storageRef.child("announcements/" + announcements.getId());
        UploadTask uploadTask = profileRef.putFile(file);
        uploadTask.addOnCompleteListener(task_storage -> {
            if (task_storage.isSuccessful()) {
                announcements.setPath("announcements/" + announcements.getId());
                mFirestore.collection("announcements")
                        .document(announcements.getId())
                        .set(announcements)
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful())
                                Log.e("TAG", "Hata: ", task.getException());
                        });
            }
        });
    }

    public void getAllAnnouncements(OnSuccessListener<ArrayList<Announcements>> onSuccessListener,
                                    OnFailureListener onFailureListener){
        ArrayList<Announcements> list = new ArrayList<>();
        Date now = new Date(), ann;
        mFirestore.collection("announcements")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        long diffInMillies, diffInDays;
                        Announcements obj;
                        for (DocumentSnapshot document : task.getResult()) {
                            obj = document.toObject(Announcements.class);
                            diffInMillies = Math.abs(now.getTime() - obj.sendDate.getTime());
                            diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                            if(diffInDays <= 5)
                                list.add(obj);
                        }
                        onSuccessListener.onSuccess(list);
                    }
                    else {
                        onFailureListener.onFailure(task.getException());
                    }
                });
    }


    public void getUserInfo(String id, OnSuccessListener<User> onSuccessListener,
                            OnFailureListener onFailureListener) {
        mFirestore.collection("users")
                .document(id)
                .get()
                .addOnCompleteListener(userInfo -> {
                    if(userInfo.isSuccessful()){
                        DocumentSnapshot document = userInfo.getResult();
                        if (document.exists()){
                            User u = document.toObject(User.class);
                            onSuccessListener.onSuccess(u);
                        }
                        else
                            onFailureListener.onFailure(new NoSuchFieldException());
                    }
                    else
                        onFailureListener.onFailure(userInfo.getException());
                });
        }

    public void saveUserInfos(Uri currentImgUri, String[] data, boolean tel, boolean mail, Context context) {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        StorageReference profileRef = storageRef.child("users/" + userId + "/profile-photo.jpg");
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try{
            formatter.parse(data[2]);            formatter.parse(data[3]);
            if (!data[5].isEmpty()) formatter.parse(data[5]);
            if (!data[6].isEmpty()) formatter.parse(data[6]);
            if (!data[8].isEmpty()) formatter.parse(data[8]);
            if (!data[9].isEmpty()) formatter.parse(data[9]);
        }
        catch (ParseException e){
            Toast.makeText(context, "Lütfen tarihleri GG/AA/YYYY formatında giriniz.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        UploadTask uploadTask = profileRef.putFile(currentImgUri);
        uploadTask.addOnCompleteListener(task_storage -> {
            if (task_storage.isSuccessful()){
                User u = new User("users/" + userId + "/profile-photo.jpg", userId,
                        data[0], data[13], data[4], data[7], data[10], data[11], data[12], data[2],
                        data[5], data[8], data[3], data[6], data[9], data[1], tel, mail);
                mFirestore.collection("users")
                        .document(userId)
                        .set(u)
                        .addOnCompleteListener(task_firestore -> {
                            if (task_firestore.isSuccessful()) {
                                if(data[0] != user.getEmail()){
                                    user.updateEmail(data[0]).addOnCompleteListener(task -> {
                                        if(task.isSuccessful())
                                            Toast.makeText(context, "Kullanıcı başarıyla kaydedildi.",
                                                    Toast.LENGTH_SHORT).show();
                                    });
                                }
                                else
                                    Toast.makeText(context, "Kullanıcı başarıyla kaydedildi.",
                                            Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("TAG", "Hata: ", task_firestore.getException());
                                Toast.makeText(context, "Kullanıcı kaydedilemedi.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Log.e("TAG", "Hata: ", task_storage.getException());
                Toast.makeText(context, "Kullanıcı kaydedilemedi.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveUserInfos(String[] data, boolean tel, boolean mail, Context context) {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try{
            formatter.parse(data[2]);            formatter.parse(data[3]);
            if (!data[5].isEmpty()) formatter.parse(data[5]);
            if (!data[6].isEmpty()) formatter.parse(data[6]);
            if (!data[8].isEmpty()) formatter.parse(data[8]);
            if (!data[9].isEmpty()) formatter.parse(data[9]);
        }
        catch (ParseException e){
            Toast.makeText(context, "Lütfen tarihleri GG/AA/YYYY formatında giriniz.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        User u = new User("users/" + userId + "/profile-photo.jpg", userId, data[0], data[13], data[4], data[7],
                data[10], data[11], data[12], data[2], data[5], data[8], data[3], data[6],
                data[9], data[1], tel, mail);
        mFirestore.collection("users")
                .document(userId)
                .set(u)
                .addOnCompleteListener(task_firestore -> {
                    if (task_firestore.isSuccessful()) {
                        if(data[0] != user.getEmail()){
                            user.updateEmail(data[0]).addOnCompleteListener(task -> {
                                if(task.isSuccessful())
                                    Toast.makeText(context, "Kullanıcı başarıyla kaydedildi.",
                                            Toast.LENGTH_SHORT).show();
                            });
                        }
                        else
                            Toast.makeText(context, "Kullanıcı başarıyla kaydedildi.",
                                    Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("TAG", "Hata: ", task_firestore.getException());
                        Toast.makeText(context, "Kullanıcı kaydedilemedi.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void getAllUsers(OnSuccessListener<ArrayList<User>> onSuccessListener,
                            OnFailureListener onFailureListener) {
        ArrayList<User> list = new ArrayList<>();
        mFirestore.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for (DocumentSnapshot document : task.getResult()) {
                            list.add(document.toObject(User.class));
                        }
                        onSuccessListener.onSuccess(list);
                    }
                    else {
                        onFailureListener.onFailure(task.getException());
                    }
                });
    }

    public ArrayList<User> getUsersViaFilter(ArrayList<User> allUsers, String filter) {
        ArrayList<User> list = new ArrayList<>();
        for(User u : allUsers)
            if(u.getFullName().toLowerCase().contains(filter) ||
                    u.getCountry().toLowerCase().contains(filter) ||
                    u.getCity().toLowerCase().contains(filter) ||
                    u.getGraduate().toString().contains(filter))
                list.add(u);
        return list;
    }

    public void addUserMedia(String userID, Uri uri, OnSuccessListener<Boolean> onSuccessListener){
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss", Locale.getDefault());
        Date now = new Date();
        String date = formatter.format(now);
        StorageReference profileRef = storageRef.child("media/" + userID + "/" + date);
        UploadTask uploadTask = profileRef.putFile(uri);
        uploadTask.addOnCompleteListener(task_storage -> {
            if (task_storage.isSuccessful())
                onSuccessListener.onSuccess(true);
            else
                onSuccessListener.onSuccess(false);
        });
    }

    public void getUserMedia(String userID, OnSuccessListener<ArrayList<Uri>> onSuccessListener, OnFailureListener onFailureListener) {
        ArrayList<Uri> mediaList = new ArrayList<>();
        String UPPER_PATH = "media/" + userID + "/";
        storage.getReference(UPPER_PATH).listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if(task.isSuccessful()){
                    List<StorageReference> items = task.getResult().getItems();
                    for(int i = 0; i < items.size(); i++){
                        items.get(i).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                mediaList.add(uri);
                                if (mediaList.size() == items.size()) {
                                    onSuccessListener.onSuccess(mediaList);
                                }
                            }
                        });
                    }
                    if (mediaList.size() == items.size()) {
                        onSuccessListener.onSuccess(mediaList);
                    }
                }
                else {
                    onFailureListener.onFailure(new Exception());
                }
            }
        });
    }

    public void getImageUri(String path, OnSuccessListener<Uri> onSuccessListener){
        storageRef.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                onSuccessListener.onSuccess(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onSuccessListener.onSuccess(null);
            }
        });
    }

    public void deleteImageFromStorage(String path, OnSuccessListener<Boolean> onSuccessListener){
        storageRef.child(path).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    onSuccessListener.onSuccess(true);
                }
                else
                    onSuccessListener.onSuccess(false);
            }
        });
    }
}

