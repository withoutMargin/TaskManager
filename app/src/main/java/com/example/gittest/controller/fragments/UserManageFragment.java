package com.example.gittest.controller.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gittest.R;
import com.example.gittest.repositories.UserDBRepository;
import com.example.gittest.adapters.UserListAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserManageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserManageFragment extends Fragment {

    private UserDBRepository mUserRepository;
    private RecyclerView mRecyclerView;
    private UserListAdapter mAdapter;


    public UserManageFragment() {
        // Required empty public constructor
    }

    public static UserManageFragment newInstance() {
        UserManageFragment fragment = new UserManageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserRepository = UserDBRepository.getInstance(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUi();

    }


    private void updateUi() {
        if (mAdapter == null)
            mAdapter = new UserListAdapter(getActivity(), getFragmentManager());
        mAdapter.setUsers(mUserRepository.getList());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void findViews(@NonNull View view) {
        mRecyclerView = view.findViewById(R.id.recyclerView_task_list);
    }
}