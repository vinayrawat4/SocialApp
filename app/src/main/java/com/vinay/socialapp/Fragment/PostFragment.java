package com.vinay.socialapp.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.vinay.socialapp.Model.PostModel;
import com.vinay.socialapp.Model.User;
import com.vinay.socialapp.R;
import com.vinay.socialapp.databinding.FragmentPostBinding;

import java.util.Date;

public class PostFragment extends Fragment {

    FragmentPostBinding binding;

    Uri uri;

    FirebaseAuth auth;
    FirebaseDatabase database;

    FirebaseStorage storage;

    public PostFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPostBinding.inflate(inflater, container, false);

        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            Picasso.get()
                                    .load(user.getProfile())
                                    .placeholder(R.drawable.baseline_image)
                                    .into(binding.profilepost);
                            binding.namep.setText(user.getName());
                            binding.profp.setText(user.getProfession());

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.postdesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String desc = binding.postdesc.getText().toString();
                if (!desc.isEmpty()) {
                    binding.postbtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.follow_btnbg));
                    binding.postbtn.setTextColor(getContext().getResources().getColor(R.color.white));
                    binding.postbtn.setEnabled(true);
                } else {
                    binding.postbtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.follow_btn));
                    binding.postbtn.setTextColor(getContext().getResources().getColor(R.color.white));
                    binding.postbtn.setEnabled(false);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        binding.addimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });


        binding.postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StorageReference reference = storage.getReference().child("posts")
                        .child(FirebaseAuth.getInstance().getUid())
                        .child(new Date().getTime()+"");
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                PostModel model = new PostModel();
                                model.setPostImage(uri.toString());
                                model.setPostedBy(FirebaseAuth.getInstance().getUid());
                                model.setPostDescription(binding.postdesc.getText().toString());
                                model.setPostedAt(new Date().getTime());

                                database.getReference().child("posts").push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        });
                    }
                });
            }
        });

        return binding.getRoot();


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getData() != null) {
            uri = data.getData();
            binding.postimg.setImageURI(uri);
            binding.postimg.setVisibility(View.VISIBLE);

            binding.postbtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.follow_btnbg));
            binding.postbtn.setTextColor(getContext().getResources().getColor(R.color.white));
            binding.postbtn.setEnabled(true);
        }
    }
}