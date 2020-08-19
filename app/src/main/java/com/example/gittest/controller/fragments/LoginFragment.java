package com.example.gittest.controller.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gittest.R;
import com.example.gittest.controller.activities.TaskPagerActivity;
import com.example.gittest.controller.activities.UserListActivity;
import com.example.gittest.model.User;
import com.example.gittest.repositories.UserRepository;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    public static final String SIGN_UP_DIALOG_FRAGMENT_TAG = "signUpDialogFragment";
    private EditText mEditTextUserName;
    private EditText mEditTextPassword;
    private Button mButtonLogin;
    private Button mButtonSignUp;
    private UserRepository mUserRepository;


    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserRepository = UserRepository.getInstance();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        setListeners();
        //init ui for fast test app
        initUi();

    }

    private void initUi() {
        mEditTextUserName.setText("a");
        mEditTextPassword.setText("1");
    }

    private void setListeners() {
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkUserExist(mEditTextUserName.getText().toString(), mEditTextPassword.getText().toString())) {
                    startActivity(TaskPagerActivity.newIntent(getActivity(), mEditTextUserName.getText().toString()));
                } else if (mEditTextUserName.getText().toString().equalsIgnoreCase("admin")
                        && mEditTextPassword.getText().toString().equalsIgnoreCase("admin")) {
                    startActivity(UserListActivity.newIntent(getActivity()));

                } else {
                    Snackbar.make(getView(), "Incorrect username or password", BaseTransientBottomBar.LENGTH_SHORT).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE).show();
                }
            }
        });
        mButtonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpDialogFragment signUpDialogFragment = SignUpDialogFragment.newInstance();
                signUpDialogFragment.show(getFragmentManager(), SIGN_UP_DIALOG_FRAGMENT_TAG);
            }
        });
    }


    private boolean checkUserExist(String userName, String passWord) {
        for (User user : mUserRepository.getList()) {
            if (user.getUserName().equals(userName) && user.getPassword().equals(passWord))
                return true;
        }
        return false;
    }

    private void findViews(View view) {
        mEditTextUserName = view.findViewById(R.id.editText_username);
        mEditTextPassword = view.findViewById(R.id.editText_password);
        mButtonLogin = view.findViewById(R.id.button_login);
        mButtonSignUp = view.findViewById(R.id.button_signUp);

    }
}