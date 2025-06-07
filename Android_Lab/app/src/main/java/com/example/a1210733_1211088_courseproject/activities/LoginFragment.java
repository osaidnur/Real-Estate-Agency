package com.example.a1210733_1211088_courseproject.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.a1210733_1211088_courseproject.R;

/**
 * Login fragment that handles user authentication
 */
public class LoginFragment extends Fragment {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private AuthCallbackInterface authCallback;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Make sure the activity implements the callback interface
        if (context instanceof AuthCallbackInterface) {
            authCallback = (AuthCallbackInterface) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement AuthCallbackInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        
        // Initialize views
        initializeViews(view);
        
        // Set up login button click listener
        setupLoginButton();
        
        return view;
    }    private void initializeViews(View view) {
        emailEditText = view.findViewById(R.id.login_email);
        passwordEditText = view.findViewById(R.id.login_password);
        loginButton = view.findViewById(R.id.btn_login);
    }

    private void setupLoginButton() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                
                // Basic validation
                if (email.isEmpty()) {
                    emailEditText.setError("Email is required");
                    emailEditText.requestFocus();
                    return;
                }
                
                if (password.isEmpty()) {
                    passwordEditText.setError("Password is required");
                    passwordEditText.requestFocus();
                    return;
                }
                
                // Call the authentication callback
                if (authCallback != null) {
                    authCallback.onLoginRequested(email, password);
                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        authCallback = null;
    }
}