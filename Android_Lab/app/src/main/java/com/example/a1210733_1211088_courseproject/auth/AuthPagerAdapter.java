package com.example.a1210733_1211088_courseproject.auth;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.a1210733_1211088_courseproject.activities.LoginFragment;
import com.example.a1210733_1211088_courseproject.activities.RegisterFragment;

/**
 * Adapter for handling authentication fragments (Login and Register)
 */
public class AuthPagerAdapter extends FragmentStateAdapter {
    
    private static final int NUM_TABS = 2;
    private static final int LOGIN_TAB = 0;
    private static final int REGISTER_TAB = 1;
    
    public AuthPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case LOGIN_TAB:
                return LoginFragment.newInstance();
            case REGISTER_TAB:
                return RegisterFragment.newInstance("", "");
            default:
                return LoginFragment.newInstance();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_TABS;
    }
    
    /**
     * Get the title for a tab at the given position
     * @param position Tab position
     * @return Tab title
     */
    public String getTabTitle(int position) {
        switch (position) {
            case LOGIN_TAB:
                return "Login";
            case REGISTER_TAB:
                return "Register";
            default:
                return "Login";
        }
    }
}
