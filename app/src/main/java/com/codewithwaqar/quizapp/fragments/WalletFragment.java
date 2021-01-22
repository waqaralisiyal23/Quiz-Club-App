package com.codewithwaqar.quizapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codewithwaqar.quizapp.CreateAccountActivity;
import com.codewithwaqar.quizapp.R;
import com.codewithwaqar.quizapp.databinding.FragmentWalletBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import models.UserModel;
import models.WithdrawRequest;

public class WalletFragment extends Fragment {

    FragmentWalletBinding binding;
    private FirebaseFirestore database;
    private UserModel user;

    public WalletFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWalletBinding.inflate(inflater, container, false);
        database = FirebaseFirestore.getInstance();

        database.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(UserModel.class);
                binding.currentCoins.setText(String.valueOf(user.getCoins()));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),
                        e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

        binding.sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.emailBox.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter email address",
                            Toast.LENGTH_SHORT).show();
                }
                else if(user.getCoins() < 50000) {
                    Toast.makeText(getContext(), "You need more coins to get withdraw.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    String uid = FirebaseAuth.getInstance().getUid();
                    String payoneer = binding.emailBox.getText().toString();
                    WithdrawRequest request = new WithdrawRequest(uid, payoneer, user.getName());
                    database.collection("withdraws").document(uid)
                            .set(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Request sent successfully.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        return binding.getRoot();
    }
}