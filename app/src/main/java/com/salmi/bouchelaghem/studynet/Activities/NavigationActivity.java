package com.salmi.bouchelaghem.studynet.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.salmi.bouchelaghem.studynet.Models.ClassItem;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.CustomLoadingDialog;

import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivityNavigationBinding;

import java.util.ArrayList;
import java.util.Map;


public class NavigationActivity extends AppCompatActivity {

    private ActivityNavigationBinding binding;
    private final CurrentUser currentUser = CurrentUser.getInstance();
    public ImageView btnFilter;

    //Loading dialog
    private CustomLoadingDialog loadingDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayList<String> dayList = new ArrayList<>();
    private ArrayList<String> classList = new ArrayList<>();

    Spinner day1Spinner, day2Spinner, class1Spinner, class2Spinner;
    Button swapButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        day1Spinner = findViewById(R.id.day1);
        day2Spinner = findViewById(R.id.day2);
        class1Spinner = findViewById(R.id.class1);
        class2Spinner = findViewById(R.id.class2);
        swapButton = findViewById(R.id.btnSwap);

        dayList.add("day1");
        dayList.add("day2");
        dayList.add("day3");
        dayList.add("day4");
        dayList.add("day5");
        dayList.add("day6");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(NavigationActivity.this, android.R.layout.simple_spinner_item, dayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        day1Spinner.setAdapter(adapter);
        day2Spinner.setAdapter(adapter);
        day1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedDay = (String) adapterView.getItemAtPosition(position);
                // Populate class1Spinner based on selectedDay
                populateClassesSpinner(selectedDay, class1Spinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle nothing selected
            }
        });

        day2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedDay = (String) adapterView.getItemAtPosition(position);
                // Populate class2Spinner based on selectedDay
                populateClassesSpinner(selectedDay, class2Spinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle nothing selected
            }
        });
        swapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected classes from the spinners
                swapClasses();
            }
        });


        //Init loading dialog
        loadingDialog = new CustomLoadingDialog(this);
        btnFilter = binding.btnFilter;
        binding.navigationView.getMenu().clear();
        binding.navigationView.inflateMenu(R.menu.drawer_admin_menu);
//        if (currentUser != null && currentUser.getUserType() != null) {
//            if (currentUser.getUserType().equals(Utils.TEACHER_ACCOUNT)) {
//                // If its a teacher then show the teacher's drawer menu
//                binding.navigationView.getMenu().clear();
//                binding.navigationView.inflateMenu(R.menu.drawer_teacher_menu);
//            } else if (currentUser.getUserType().equals(Utils.ADMIN_ACCOUNT)) {
//                // If its a admin then show the admin drawer menu
//                binding.navigationView.getMenu().clear();
//                binding.navigationView.inflateMenu(R.menu.drawer_admin_menu);
//            }
//        } else {
//            Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
//            // Close the app
//            finishAffinity();
//        }

        // If its a student then the default drawer menu will do
        binding.btnOpenDrawer.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));

        NavController navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupWithNavController(binding.navigationView, navController);

        // Change toolbar title to the fragment's title
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> binding.toolBarTitle.setText(destination.getLabel()));

        MenuItem btnLogout = binding.navigationView.getMenu().findItem(R.id.nav_logout);
        btnLogout.setOnMenuItemClickListener(item -> {
            if (currentUser != null && currentUser.getUserType() != null) {
                loadingDialog.show();
                clearUserState();
            } else {
                // Take the user to the login page.
                clearUserState();

                FirebaseAuth.getInstance().signOut();
                Toast.makeText(NavigationActivity.this, getString(R.string.logout_msg), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NavigationActivity.this, LoginActivity.class));
                finish();
            }
            return true;
        });
    }

    private void populateClassesSpinner(String selectedDay, Spinner spinner) {
        db.collection("timetable").document(selectedDay).collection("classes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> classList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String className = document.getString("subject");
                                classList.add(className);
                            }
                            // Set classList to spinner's adapter
                            ArrayAdapter<String> classAdapter = new ArrayAdapter<>(NavigationActivity.this, android.R.layout.simple_spinner_item, classList);
                            classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(classAdapter);
                        } else {
//                                Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    private void clearUserState() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Utils.USER_LOGGED_IN, false);
        // Remove any other user-related data from shared preferences if needed
        editor.apply();
    }
    private void swapClasses() {
        // Get the selected days and classes from spinners
        String day1 = day1Spinner.getSelectedItem().toString();
        String day2 = day2Spinner.getSelectedItem().toString();
        String class1 = class1Spinner.getSelectedItem().toString();
        String class2 = class2Spinner.getSelectedItem().toString();

        // Retrieve the document IDs of the selected classes
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference timetableCollection = db.collection("timetable");

        timetableCollection.document(day1).collection("classes")
                .whereEqualTo("subject", class1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String docId1 = queryDocumentSnapshots.getDocuments().get(0).getId();
                            timetableCollection.document(day2).collection("classes")
                                    .whereEqualTo("subject", class2)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                String docId2 = queryDocumentSnapshots.getDocuments().get(0).getId();

                                                // Swap the data of the selected classes in Firestore
                                                swapClassesInFirestore(day1, day2, docId1, docId2);
                                            } else {
//                                                Log.d(TAG, "No document found for " + day2 + " - " + class2);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
//                                            Log.e(TAG, "Error getting documents for " + day2 + " - " + class2, e);
                                        }
                                    });
                        } else {
//                            Log.d(TAG, "No document found for " + day1 + " - " + class1);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Log.e(TAG, "Error getting documents for " + day1 + " - " + class1, e);
                    }
                });
    }

    private void swapClassesInFirestore(String day1, String day2, String docId1, String docId2) {
        // Perform the swap operation in Firestore
        // Retrieve and update the data of the selected classes using their document IDs
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference timetableCollection = db.collection("timetable");

        timetableCollection.document(day1).collection("classes").document(docId1).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> classData1 = documentSnapshot.getData();

                        // Retrieve data of class 2
                        timetableCollection.document(day2).collection("classes").document(docId2).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Map<String, Object> classData2 = documentSnapshot.getData();

                                        // Swap subject and type fields
                                        String subject1 = (String) classData1.get("subject");
                                        String type1 = (String) classData1.get("type");
                                        String subject2 = (String) classData2.get("subject");
                                        String type2 = (String) classData2.get("type");

                                        classData1.put("subject", subject2);
                                        classData1.put("type", type2);
                                        classData2.put("subject", subject1);
                                        classData2.put("type", type1);

                                        // Update class 2 with class 1 data
                                        timetableCollection.document(day2).collection("classes").document(docId1).set(classData2)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // Update class 1 with class 2 data
                                                        timetableCollection.document(day1).collection("classes").document(docId2).set(classData1)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        // Notify user that swap operation is successful
                                                                        Toast.makeText(NavigationActivity.this, "Classes swapped successfully", Toast.LENGTH_SHORT).show();
//                                                                        showBookmark(day1, day2, docId1, docId2);
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
//                                                                        Log.e(TAG, "Error updating class 2", e);
                                                                    }
                                                                });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
//                                                        Log.e(TAG, "Error updating class 1", e);
                                                    }
                                                });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
//                                        Log.e(TAG, "Error getting class 2 data", e);
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Log.e(TAG, "Error getting class 1 data", e);
                    }
                });

    }
//    private void showBookmark(String day1, String day2, String docId1, String docId2) {
//        // Show bookmark on swapped classes
//        // Loop through the itemList to find the classes with matching day and docId
//        for (ClassItem item : itemList) {
//            if (item.getDay().equals(day1) && item.getDocId().equals(docId1)) {
//                item.setBookmark(true); // Set bookmark to true for class 1
//            } else if (item.getDay().equals(day2) && item.getDocId().equals(docId2)) {
//                item.setBookmark(true); // Set bookmark to true for class 2
//            }
//        }
//    }
}