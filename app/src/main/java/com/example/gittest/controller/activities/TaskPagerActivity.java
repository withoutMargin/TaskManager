package com.example.gittest.controller.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.gittest.R;
import com.example.gittest.controller.fragments.AddTaskDialogFragment;
import com.example.gittest.controller.fragments.TaskListFragment;
import com.example.gittest.enums.Role;
import com.example.gittest.enums.State;
import com.example.gittest.model.User;
import com.example.gittest.repositories.TaskDBRepository;
import com.example.gittest.repositories.UserDBRepository;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TaskPagerActivity extends AppCompatActivity implements AddTaskDialogFragment.OnAddDialogDismissListener {

    public static final String EXTRA_USERNAME = "userName";
    public static final String ADD_TASK_DIALOG_FRAGMENT_TAG = "AddTaskDialogFragment";
    public static final String BUNDLE_USERNAME = "username";

    private TabLayout mTabLayout;
    private FloatingActionButton mFloatingActionButton;
    private TaskListFragment mTodoFragment;
    private TaskListFragment mDoingFragment;
    private TaskListFragment mDoneFragment;
    private ViewPager2 mViewPager2;
    private UserDBRepository mUserDBRepository;
    private TaskDBRepository mTaskDBRepository;
    private String mUserName;
    private User mUser;


    public static Intent newIntent(Context context, String userName) {
        Intent intent = new Intent(context, TaskPagerActivity.class);
        intent.putExtra(EXTRA_USERNAME, userName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserName = getIntent().getStringExtra(EXTRA_USERNAME);

        if (savedInstanceState != null) {
            mUserName = savedInstanceState.getString(BUNDLE_USERNAME);
        }

        mUserDBRepository = UserDBRepository.getInstance(this);
        mTaskDBRepository = TaskDBRepository.getInstance(this);
        mUser = mUserDBRepository.get(mUserName);

        initFragments();


        setContentView(R.layout.activity_task_pager);
        findViews();

        if (mUser.getRole() == Role.ADMIN)
            mFloatingActionButton.setVisibility(View.GONE);

        FragmentStateAdapter adapter = new TaskViewPagerAdapter(this);
        mViewPager2.setOffscreenPageLimit(2);
        mViewPager2.setAdapter(adapter);
        new TabLayoutMediator(mTabLayout, mViewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0)
                    tab.setText(R.string.TODO);
                if (position == 1)
                    tab.setText(R.string.DOING);
                if (position == 2)
                    tab.setText(R.string.DONE);
            }
        }).attach();
        setListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        notifyAllAdapter();
    }

    private void initFragments() {
        if (mTodoFragment == null)
            mTodoFragment = TaskListFragment.newInstance(State.TODO, mUserName);
        if (mDoingFragment == null)
            mDoingFragment = TaskListFragment.newInstance(State.DOING, mUserName);
        if (mDoneFragment == null)
            mDoneFragment = TaskListFragment.newInstance(State.DONE, mUserName);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_USERNAME, mUserName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.pager_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mUser.getRole().equals(Role.ADMIN)) {
            menu.findItem(R.id.menu_item_all_users).setVisible(true);
            menu.findItem(R.id.remove_all_task_menu_item).setVisible(false);
        } else
            menu.findItem(R.id.menu_item_all_users).setVisible(false);
        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_menu_item:
                finish();
                return true;
            case R.id.remove_all_task_menu_item:
                new MaterialAlertDialogBuilder(this)
                        .setMessage(R.string.sureAlertMessage)
                        .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                            mTaskDBRepository.deleteAll();
                            notifyAllAdapter();
                        })
                        .setNeutralButton(android.R.string.cancel, null)
                        .create()
                        .show();
                return true;
            case R.id.search_menu_item:
                startActivity(SearchActivity.newIntent(this, mUserName));
                return true;

            case R.id.menu_item_all_users:
                startActivity(UserManageActivity.newIntent(this));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setListeners() {
        mFloatingActionButton.setOnClickListener(view -> {
            AddTaskDialogFragment addTaskDialogFragment = AddTaskDialogFragment.newInstance(mUserName);
            addTaskDialogFragment.show(getSupportFragmentManager(), ADD_TASK_DIALOG_FRAGMENT_TAG);
        });
    }


    private void findViews() {
        mFloatingActionButton = findViewById(R.id.fab);
        mViewPager2 = findViewById(R.id.viewPager2);
        mTabLayout = findViewById(R.id.tabLayout);
        Drawable myFabSrc = getResources().getDrawable(R.drawable.ic_add_white);
        Drawable willBeWhite = myFabSrc.getConstantState().newDrawable();
        willBeWhite.mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        mFloatingActionButton.setImageDrawable(willBeWhite);
    }


    @Override
    public void onListChanged() {
        notifyAllAdapter();
    }

    private void notifyAllAdapter() {
        if (mTodoFragment.getAdapter() != null) {
            mTodoFragment.getAdapter().setTasks(mTaskDBRepository.getSpecialTaskList(State.TODO, mUser.getId()));
            mTodoFragment.getAdapter().notifyDataSetChanged();
            mTodoFragment.changeVisibility();
        }

        if (mDoingFragment.getAdapter() != null) {
            mDoingFragment.getAdapter().setTasks(mTaskDBRepository.getSpecialTaskList(State.DOING, mUser.getId()));
            mDoingFragment.getAdapter().notifyDataSetChanged();
            mDoingFragment.changeVisibility();
        }
        if (mDoneFragment.getAdapter() != null) {
            mDoneFragment.getAdapter().setTasks(mTaskDBRepository.getSpecialTaskList(State.DONE, mUser.getId()));
            mDoneFragment.getAdapter().notifyDataSetChanged();
            mDoneFragment.changeVisibility();
        }
    }


    public class TaskViewPagerAdapter extends FragmentStateAdapter {


        public TaskViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return mTodoFragment;
            } else if (position == 1)
                return mDoingFragment;
            else if (position == 2)
                return mDoneFragment;

            return null;
        }

        @Override
        public int getItemCount() {
            return 3;
        }

    }


}

