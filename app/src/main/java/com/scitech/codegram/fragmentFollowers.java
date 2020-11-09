package com.scitech.codegram;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class fragmentFollowers extends Fragment {

    View view;
    private RecyclerView mResultList;
    FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase, followDatabase;
    private FollowManageAdapter followAdapter;
    private List<Codegram_user> userList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_followers, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mUserDatabase = FirebaseDatabase.getInstance().getReference("CodeStalk_users");
        followDatabase = FirebaseDatabase.getInstance().getReference("Follow_Management");

        mAuth = FirebaseAuth.getInstance();

        mResultList = (RecyclerView)getView().findViewById(R.id.followers_recyclerView);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(getContext()));
        userList = new ArrayList<>();
        followAdapter = new FollowManageAdapter(userList);

        mResultList.setAdapter(followAdapter);
        //userList.clear();
        followAdapter.notifyDataSetChanged();

        firebaseFollowerSearch();
    }


    public void firebaseFollowerSearch()
    {
        final FirebaseUser user = mAuth.getCurrentUser();
        followDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                for(DataSnapshot walker : dataSnapshot.getChildren())
                {
                    if(walker.getKey().equals(user.getUid()))
                    {
                        final Follow follow = walker.getValue(Follow.class);

                        mUserDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                                List<String>list = follow.getFollowers();
                                for(String follower : list) {
                                    Codegram_user cUser = dataSnapshot.child(follower).getValue(Codegram_user.class);
                                    userList.add(cUser);
                                    //Toast.makeText(getContext(), cUser.getUsername(), Toast.LENGTH_SHORT).show();
                                }
                                followAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NotNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                followAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });

    }
}
