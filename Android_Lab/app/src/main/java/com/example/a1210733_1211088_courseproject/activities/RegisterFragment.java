package com.example.a1210733_1211088_courseproject.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.example.a1210733_1211088_courseproject.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Registration fragment that handles user registration
 */
public class RegisterFragment extends Fragment {    // UI elements
    private EditText emailEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Spinner genderSpinner;
    private Spinner countrySpinner;
    private Spinner citySpinner;
    private EditText phoneEditText;
    private Button registerButton;
    
    // Callback interface
    private AuthCallbackInterface authCallback;
    
    // Country and city data
    private Map<String, String[]> countryCityMap;
    private Map<String, String> countryCodeMap;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        
        // Initialize views
        initializeViews(view);
        
        // Initialize country and city data
        initializeCountryCityData();
        
        // Setup spinners
        setupSpinners();
        
        // Setup register button
        setupRegisterButton();
        
        return view;
    }

    private void initializeViews(View view) {
        emailEditText = view.findViewById(R.id.reg_email);
        firstNameEditText = view.findViewById(R.id.reg_fname);
        lastNameEditText = view.findViewById(R.id.reg_lname);
        passwordEditText = view.findViewById(R.id.reg_password);
        confirmPasswordEditText = view.findViewById(R.id.reg_confirm);
        genderSpinner = view.findViewById(R.id.reg_gender);
        countrySpinner = view.findViewById(R.id.reg_country);
        citySpinner = view.findViewById(R.id.reg_city);
        phoneEditText = view.findViewById(R.id.reg_phone);
        registerButton = view.findViewById(R.id.btn_register);
    }

    private void initializeCountryCityData() {
        // Initialize country-city mapping
        countryCityMap = new HashMap<>();
        countryCityMap.put("Palestine", new String[]{"Select City", "Gaza", "Ramallah", "Hebron"});
        countryCityMap.put("Jordan", new String[]{"Select City", "Amman", "Irbid", "Aqaba"});
        countryCityMap.put("Syria", new String[]{"Select City", "Damascus", "Halab", "Homs"});
        
        // Initialize country code mapping
        countryCodeMap = new HashMap<>();
        countryCodeMap.put("Palestine", "+970");
        countryCodeMap.put("Jordan", "+962");
        countryCodeMap.put("Syria", "+963");
    }
    private void setupSpinners() {
        // Gender spinner
        String[] genders = {"Select Gender", "Male", "Female"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(getContext(), 
                android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Country spinner - only Palestine, Jordan, Syria
        String[] countries = {"Select Country", "Palestine", "Jordan", "Syria"};
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(getContext(), 
                android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countryAdapter);

        // Set up country selection listener to update cities
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCountry = parent.getItemAtPosition(position).toString();
                updateCitySpinner(selectedCountry);
                updatePhoneCountryCode(selectedCountry);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default cities when no country is selected
                updateCitySpinner("Select Country");
            }
        });        // Initialize city spinner with default values
        updateCitySpinner("Select Country");
    }

    private void updateCitySpinner(String selectedCountry) {
        String[] cities;
        
        if (countryCityMap.containsKey(selectedCountry)) {
            cities = countryCityMap.get(selectedCountry);
        } else {
            // Default cities when no country is selected
            cities = new String[]{"Select City"};
        }
        
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(getContext(), 
                android.R.layout.simple_spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);
    }

    private void updatePhoneCountryCode(String selectedCountry) {
        if (countryCodeMap.containsKey(selectedCountry)) {
            String countryCode = countryCodeMap.get(selectedCountry);
            String currentPhone = phoneEditText.getText().toString().trim();
            
            // Remove existing country code if present
            for (String code : countryCodeMap.values()) {
                if (currentPhone.startsWith(code)) {
                    currentPhone = currentPhone.substring(code.length()).trim();
                    break;
                }
            }
            
            // Add new country code
            phoneEditText.setText(countryCode + " " + currentPhone);
        }
    }

    private void setupRegisterButton() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get form data
                String email = emailEditText.getText().toString().trim();
                String firstName = firstNameEditText.getText().toString().trim();
                String lastName = lastNameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();
                
                // Get spinner selections
                String gender = genderSpinner.getSelectedItem().toString();
                String country = countrySpinner.getSelectedItem().toString();
                String city = citySpinner.getSelectedItem().toString();
                String phone = phoneEditText.getText().toString().trim();
                
                // Reset selection for default values
                if (gender.equals("Select Gender")) gender = "";
                if (country.equals("Select Country")) country = "";
                if (city.equals("Select City")) city = "";
                
                // Basic validation
                if (email.isEmpty()) {
                    emailEditText.setError("Email is required");
                    emailEditText.requestFocus();
                    return;
                }
                
                if (firstName.isEmpty()) {
                    firstNameEditText.setError("First name is required");
                    firstNameEditText.requestFocus();
                    return;
                }
                
                if (lastName.isEmpty()) {
                    lastNameEditText.setError("Last name is required");
                    lastNameEditText.requestFocus();
                    return;
                }
                
                if (password.isEmpty()) {
                    passwordEditText.setError("Password is required");
                    passwordEditText.requestFocus();
                    return;
                }
                
                if (confirmPassword.isEmpty()) {
                    confirmPasswordEditText.setError("Please confirm your password");
                    confirmPasswordEditText.requestFocus();
                    return;
                }
                  // Call the registration callback
                if (authCallback != null) {
                    authCallback.onRegisterRequested(email, firstName, lastName, 
                            password, confirmPassword, gender, country, city, phone);
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