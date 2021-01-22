package com.codewithwaqar.quizapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codewithwaqar.quizapp.R;
import com.codewithwaqar.quizapp.SpinnerActivity;
import com.codewithwaqar.quizapp.databinding.FragmentHomeBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import adapters.CategoryAdapter;
import models.CategoryModel;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    private RewardedAd rewardedAd;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        rewardedAd = new RewardedAd(getContext(),
                "ca-app-pub-2854521211489219/1138259028");
        rewardedAd.loadAd(new AdRequest.Builder().build(), new RewardedAdLoadCallback(){
            @Override
            public void onRewardedAdFailedToLoad(LoadAdError loadAdError) {
                super.onRewardedAdFailedToLoad(loadAdError);
            }

            @Override
            public void onRewardedAdLoaded() {
                super.onRewardedAdLoaded();
            }
        });

        final ArrayList<CategoryModel> categories = new ArrayList<>();
        final CategoryAdapter adapter = new CategoryAdapter(getContext(), categories);

        FirebaseFirestore.getInstance().collection("categories").orderBy("index")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value!=null){
                            categories.clear();
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                CategoryModel model = snapshot.toObject(CategoryModel.class);
                                model.setCategoryId(snapshot.getId());
                                categories.add(model);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

        binding.categoryList.setLayoutManager(new GridLayoutManager(getContext(),2));
        binding.categoryList.setAdapter(adapter);

        binding.spinwheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rewardedAd.isLoaded()){
                    rewardedAd.show(getActivity(), new RewardedAdCallback() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            startActivity(new Intent(getContext(), SpinnerActivity.class));
                        }
                    });
                }
                else{
                    Toast.makeText(getContext(), "Sorry, No Ad is avialable",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        return binding.getRoot();
    }
}