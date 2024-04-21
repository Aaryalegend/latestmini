package com.salmi.bouchelaghem.studynet.Fragments;


import static android.app.ProgressDialog.show;

import android.content.Context;
import android.content.Intent;

import android.graphics.Color;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;

import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.salmi.bouchelaghem.studynet.Activities.AddClassActivity;
import com.salmi.bouchelaghem.studynet.Activities.NavigationActivity;
import com.salmi.bouchelaghem.studynet.Activities.SignUpActivity;
import com.salmi.bouchelaghem.studynet.Adapters.MyAdapter;
import com.salmi.bouchelaghem.studynet.Adapters.SessionsAdapter;
import com.salmi.bouchelaghem.studynet.Models.ClassItem;
import com.salmi.bouchelaghem.studynet.Models.Section;
import com.salmi.bouchelaghem.studynet.Models.Session;
import com.salmi.bouchelaghem.studynet.Models.Student;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.CustomLoadingDialog;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.FragmentTimetableBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class TimetableFragment extends Fragment {

    private FragmentTimetableBinding binding;
    private int currentDay = 1;
    private List<String> days;

    // Rec view


    private boolean filterApplied = true;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTimetableBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initRecView();

        NavigationActivity context = (NavigationActivity) getActivity();
        assert context != null;

//        binding.selectSectionMsg.setVisibility(View.VISIBLE);

        binding.btnAdd.setVisibility(View.VISIBLE);
        binding.btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddClassActivity.class);
//            intent.putExtra(Utils.ACTION, Utils.ACTION_ADD);
            startActivity(intent);
        });
        goToToday();
        // Init days
        days = Arrays.asList(getResources().getStringArray(R.array.days));

        // Change the day by clicking on the wanted day
        binding.day1.setOnClickListener(v -> goToDay1());

        binding.day2.setOnClickListener(v -> goToDay2());

        binding.day3.setOnClickListener(v -> goToDay3());

        binding.day4.setOnClickListener(v -> goToDay4());

        binding.day5.setOnClickListener(v -> goToDay5());

        binding.day6.setOnClickListener(v -> goToDay6());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        if (filterApplied) {
//            getSessions(selectedSection);
//        }
    }

    private void initRecView() {
        Context context = requireContext(); // Use requireContext() to ensure a non-null context
        binding.classesRecView.setLayoutManager(new LinearLayoutManager(context));
        binding.classesRecView.addItemDecoration(new DividerItemDecoration(context, LinearLayout.VERTICAL));
//        adapter = new SessionsAdapter((List<String>) context);
    }

    /* Go to day X methods */
    private void goToToday() {
        // Used ViewTreeObserver to wait for the UI to be sized and then we can get the the view's width
        binding.day2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.day2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // Show today's classes by default
                Calendar calendar = Calendar.getInstance();
                switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                    case 1:
                    case 2: //Monday
                        goToDay1();
                        break;
                    case 3://Tuesday
                        goToDay2();
                        break;
                    case 4:
                        goToDay3();
                        break;
                    case 5:
                        goToDay4();
                        break;
                    case 6:
                        goToDay5();
                        break;
                    case 7:
                        goToDay6();
                        break;
                }
            }
        });
    }

    private void goToDay1() {
        currentDay = 1;
        // Adding an animation
        binding.selectedDay.animate().x(0).setDuration(100);
        // Changing text colors
        binding.day1.setTextColor(Color.WHITE);
        binding.day2.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day3.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day4.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day5.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day6.setTextColor(getResources().getColor(R.color.secondary_text_color, null));

        // Setting the day's name
        binding.txtSelectedDay.setText(days.get(0));

        // Show today's sessions only if the user is a student or the teacher/admin applied a filter
        if (filterApplied) {
            // Show today's classes
            showTodaySessions(1);
        }
    }

    private void goToDay2() {
        currentDay = 2;
        int size = binding.day2.getWidth();
        binding.selectedDay.animate().x(size).setDuration(100);
        binding.day1.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day2.setTextColor(Color.WHITE);
        binding.day3.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day4.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day5.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day6.setTextColor(getResources().getColor(R.color.secondary_text_color, null));

        // Setting the day's name
        binding.txtSelectedDay.setText(days.get(1));

        // Show today's sessions only if the user is a student or the teacher/admin applied a filter
        if ( filterApplied) {
            // Show today's classes
            showTodaySessions(2);
        }
    }

    private void goToDay3() {
        currentDay = 3;
        int size = binding.day2.getWidth() * 2;
        binding.selectedDay.animate().x(size).setDuration(100);
        binding.day1.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day2.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day3.setTextColor(Color.WHITE);
        binding.day4.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day5.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day6.setTextColor(getResources().getColor(R.color.secondary_text_color, null));

        // Setting the day's name
        binding.txtSelectedDay.setText(days.get(2));

        // Show today's sessions only if the user is a student or the teacher/admin applied a filter
        if (filterApplied) {
            // Show today's classes
            showTodaySessions(3);
        }
    }

    private void goToDay4() {
        currentDay = 4;
        int size = binding.day2.getWidth() * 3;
        binding.selectedDay.animate().x(size).setDuration(100);
        binding.day1.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day2.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day3.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day4.setTextColor(Color.WHITE);
        binding.day5.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day6.setTextColor(getResources().getColor(R.color.secondary_text_color, null));

        // Setting the day's name
        binding.txtSelectedDay.setText(days.get(3));

        // Show today's sessions only if the user is a student or the teacher/admin applied a filter
        if (filterApplied) {
            // Show today's classes
            showTodaySessions(4);
        }
    }

    private void goToDay5() {
        currentDay = 5;
        int size = binding.day2.getWidth() * 4;
        binding.selectedDay.animate().x(size).setDuration(100);
        binding.day1.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day2.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day3.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day4.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day5.setTextColor(Color.WHITE);
        binding.day6.setTextColor(getResources().getColor(R.color.secondary_text_color, null));

        // Setting the day's name
        binding.txtSelectedDay.setText(days.get(4));

        // Show today's sessions only if the user is a student or the teacher/admin applied a filter
        if (filterApplied) {
            // Show today's classes
            showTodaySessions(5);
        }
    }

    private void goToDay6() {
        currentDay = 6;
        int size = binding.day2.getWidth() * 5;
        binding.selectedDay.animate().x(size).setDuration(100);
        binding.day1.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day2.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day3.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day4.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day5.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day6.setTextColor(Color.WHITE);

        // Setting the day's name
        binding.txtSelectedDay.setText(days.get(5));

        // Show today's sessions only if the user is a student or the teacher/admin applied a filter
        if (filterApplied) {
            // Show today's classes
            showTodaySessions(6);
        }
    }

    /* Sessions methods */
    private void getSessions(String sectionCode) {
        // Start loading animation
//        binding.loadingAnimation.setVisibility(View.VISIBLE);
//        binding.loadingAnimation.playAnimation();
        // Get all the sessions for this section
//        Call<List<Session>> getSectionSessionsCall = api.getSectionSessions(sectionCode, "Token " + currentUser.getToken());

    }
    String collectionName = "timetable";
    public void showTodaySessions(int today) {
        List<ClassItem> todaySessions = new ArrayList<>();
        String dayDocumentId;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        switch(today) {
            // Dummy data for demonstration
            case 1:
                dayDocumentId = "day1"; // Change this to match the document ID for the desired day
                db.collection(collectionName).document(dayDocumentId)
                        .collection("classes") // Assuming classes are stored in a subcollection
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // Convert Firestore document to ClassItem object
                                        ClassItem classItem = document.toObject(ClassItem.class);
                                        // Add ClassItem object to todaySessions list
                                        todaySessions.add(classItem);
                                    }
                                    if (!todaySessions.isEmpty()) {
                                        MyAdapter adapter = new MyAdapter(todaySessions); // MyAdapter is the RecyclerView adapter class
                                        binding.classesRecView.setAdapter(adapter);
                                        binding.classesRecView.setVisibility(View.VISIBLE);
                                        binding.emptyMsg.setVisibility(View.GONE);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        binding.classesRecView.setVisibility(View.GONE);
                                        binding.emptyMsg.setVisibility(View.VISIBLE);
                                    }
                                    int sessionsCount = 0;
                                    sessionsCount = todaySessions.size(); // Assuming todaySessions is the list of sessions for the day

                                    if (sessionsCount == 1) {
                                        binding.textView4.setText(getString(R.string.class_1)); // Assuming R.string.class_1 contains the string "class"
                                    } else {
                                        binding.textView4.setText(getString(R.string.classes)); // Assuming R.string.classes contains the string "classes"
                                    }
                                    binding.txtClassesCount.setText(String.valueOf(sessionsCount));
                                }
                            }
                        });

                // Show the sessions in the recycler view

                break;
            case 2:
                dayDocumentId = "day2"; // Change this to match the document ID for the desired day
                db.collection(collectionName).document(dayDocumentId)
                        .collection("classes") // Assuming classes are stored in a subcollection
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // Convert Firestore document to ClassItem object
                                        ClassItem classItem = document.toObject(ClassItem.class);
                                        // Add ClassItem object to todaySessions list
                                        todaySessions.add(classItem);
                                    }
                                    if (!todaySessions.isEmpty()) {
                                        MyAdapter adapter = new MyAdapter(todaySessions); // MyAdapter is the RecyclerView adapter class
                                        binding.classesRecView.setAdapter(adapter);
                                        binding.classesRecView.setVisibility(View.VISIBLE);
                                        binding.emptyMsg.setVisibility(View.GONE);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        binding.classesRecView.setVisibility(View.GONE);
                                        binding.emptyMsg.setVisibility(View.VISIBLE);
                                    }
                                    int sessionsCount = 0;
                                    sessionsCount = todaySessions.size(); // Assuming todaySessions is the list of sessions for the day

                                    if (sessionsCount == 1) {
                                        binding.textView4.setText(getString(R.string.class_1)); // Assuming R.string.class_1 contains the string "class"
                                    } else {
                                        binding.textView4.setText(getString(R.string.classes)); // Assuming R.string.classes contains the string "classes"
                                    }
                                    binding.txtClassesCount.setText(String.valueOf(sessionsCount));
                                }
                            }
                        });
//                todaySessions.add(new ClassItem("1", "8:00 AM", "9:30 AM", "Computer Networks", "Lecturer A"));
//                todaySessions.add(new ClassItem("2", "9:45 AM", "11:15 AM", "Software Engineering", "Lecturer B"));
//                todaySessions.add(new ClassItem("3", "11:30 AM", "1:00 PM", "Computer Architecture", "Lecturer C"));

                // Show the sessions in the recycler view

                break;
            case 3:
                dayDocumentId = "day3"; // Change this to match the document ID for the desired day
                db.collection(collectionName).document(dayDocumentId)
                        .collection("classes") // Assuming classes are stored in a subcollection
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // Convert Firestore document to ClassItem object
                                        ClassItem classItem = document.toObject(ClassItem.class);
                                        // Add ClassItem object to todaySessions list
                                        todaySessions.add(classItem);
                                    }
                                    if (!todaySessions.isEmpty()) {
                                        MyAdapter adapter = new MyAdapter(todaySessions); // MyAdapter is the RecyclerView adapter class
                                        binding.classesRecView.setAdapter(adapter);
                                        binding.classesRecView.setVisibility(View.VISIBLE);
                                        binding.emptyMsg.setVisibility(View.GONE);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        binding.classesRecView.setVisibility(View.GONE);
                                        binding.emptyMsg.setVisibility(View.VISIBLE);
                                    }
                                    int sessionsCount = 0;
                                    sessionsCount = todaySessions.size(); // Assuming todaySessions is the list of sessions for the day

                                    if (sessionsCount == 1) {
                                        binding.textView4.setText(getString(R.string.class_1)); // Assuming R.string.class_1 contains the string "class"
                                    } else {
                                        binding.textView4.setText(getString(R.string.classes)); // Assuming R.string.classes contains the string "classes"
                                    }
                                    binding.txtClassesCount.setText(String.valueOf(sessionsCount));
                                }
                            }
                        });
//                todaySessions.add(new ClassItem("1", "8:00 AM", "9:30 AM", "Computer Networks", "Lecturer A"));
//                todaySessions.add(new ClassItem("2", "9:45 AM", "11:15 AM", "Software Engineering", "Lecturer B"));
//                todaySessions.add(new ClassItem("3", "11:30 AM", "1:00 PM", "Computer Architecture", "Lecturer C"));

                // Show the sessions in the recycler view

                break;
            case 4:
                dayDocumentId = "day4"; // Change this to match the document ID for the desired day
                db.collection(collectionName).document(dayDocumentId)
                        .collection("classes") // Assuming classes are stored in a subcollection
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // Convert Firestore document to ClassItem object
                                        ClassItem classItem = document.toObject(ClassItem.class);
                                        // Add ClassItem object to todaySessions list
                                        todaySessions.add(classItem);
                                    }
                                    if (!todaySessions.isEmpty()) {
                                        MyAdapter adapter = new MyAdapter(todaySessions); // MyAdapter is the RecyclerView adapter class
                                        binding.classesRecView.setAdapter(adapter);
                                        binding.classesRecView.setVisibility(View.VISIBLE);
                                        binding.emptyMsg.setVisibility(View.GONE);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        binding.classesRecView.setVisibility(View.GONE);
                                        binding.emptyMsg.setVisibility(View.VISIBLE);
                                    }
                                    int sessionsCount = 0;
                                    sessionsCount = todaySessions.size(); // Assuming todaySessions is the list of sessions for the day

                                    if (sessionsCount == 1) {
                                        binding.textView4.setText(getString(R.string.class_1)); // Assuming R.string.class_1 contains the string "class"
                                    } else {
                                        binding.textView4.setText(getString(R.string.classes)); // Assuming R.string.classes contains the string "classes"
                                    }
                                    binding.txtClassesCount.setText(String.valueOf(sessionsCount));
                                }
                            }
                        });
//                todaySessions.add(new ClassItem("1", "8:00 AM", "9:30 AM", "Computer Networks", "Lecturer A"));
//                todaySessions.add(new ClassItem("2", "9:45 AM", "11:15 AM", "Software Engineering", "Lecturer B"));
//                todaySessions.add(new ClassItem("3", "11:30 AM", "1:00 PM", "Computer Architecture", "Lecturer C"));

                // Show the sessions in the recycler view

                break;
            case 5:
                dayDocumentId = "day5"; // Change this to match the document ID for the desired day
                db.collection(collectionName).document(dayDocumentId)
                        .collection("classes") // Assuming classes are stored in a subcollection
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // Convert Firestore document to ClassItem object
                                        ClassItem classItem = document.toObject(ClassItem.class);
                                        // Add ClassItem object to todaySessions list
                                        todaySessions.add(classItem);
                                    }
                                    if (!todaySessions.isEmpty()) {
                                        MyAdapter adapter = new MyAdapter(todaySessions); // MyAdapter is the RecyclerView adapter class
                                        binding.classesRecView.setAdapter(adapter);
                                        binding.classesRecView.setVisibility(View.VISIBLE);
                                        binding.emptyMsg.setVisibility(View.GONE);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        binding.classesRecView.setVisibility(View.GONE);
                                        binding.emptyMsg.setVisibility(View.VISIBLE);
                                    }
                                    int sessionsCount = 0;
                                    sessionsCount = todaySessions.size(); // Assuming todaySessions is the list of sessions for the day

                                    if (sessionsCount == 1) {
                                        binding.textView4.setText(getString(R.string.class_1)); // Assuming R.string.class_1 contains the string "class"
                                    } else {
                                        binding.textView4.setText(getString(R.string.classes)); // Assuming R.string.classes contains the string "classes"
                                    }
                                    binding.txtClassesCount.setText(String.valueOf(sessionsCount));
                                }
                            }
                        });
//                todaySessions.add(new ClassItem("1", "8:00 AM", "9:30 AM", "Computer Networks", "Lecturer A"));
//                todaySessions.add(new ClassItem("2", "9:45 AM", "11:15 AM", "Software Engineering", "Lecturer B"));
//                todaySessions.add(new ClassItem("3", "11:30 AM", "1:00 PM", "Computer Architecture", "Lecturer C"));

                // Show the sessions in the recycler view

                break;
            case 6:
                dayDocumentId = "day6"; // Change this to match the document ID for the desired day
                db.collection(collectionName).document(dayDocumentId)
                        .collection("classes") // Assuming classes are stored in a subcollection
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // Convert Firestore document to ClassItem object
                                        ClassItem classItem = document.toObject(ClassItem.class);
                                        // Add ClassItem object to todaySessions list
                                        todaySessions.add(classItem);
                                    }
                                    if (!todaySessions.isEmpty()) {
                                        MyAdapter adapter = new MyAdapter(todaySessions); // MyAdapter is the RecyclerView adapter class
                                        binding.classesRecView.setAdapter(adapter);
                                        binding.classesRecView.setVisibility(View.VISIBLE);
                                        binding.emptyMsg.setVisibility(View.GONE);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        binding.classesRecView.setVisibility(View.GONE);
                                        binding.emptyMsg.setVisibility(View.VISIBLE);
                                    }
                                    int sessionsCount = 0;
                                    sessionsCount = todaySessions.size(); // Assuming todaySessions is the list of sessions for the day

                                    if (sessionsCount == 1) {
                                        binding.textView4.setText(getString(R.string.class_1)); // Assuming R.string.class_1 contains the string "class"
                                    } else {
                                        binding.textView4.setText(getString(R.string.classes)); // Assuming R.string.classes contains the string "classes"
                                    }
                                    binding.txtClassesCount.setText(String.valueOf(sessionsCount));
                                }
                            }
                        });
//                todaySessions.add(new ClassItem("1", "8:00 AM", "9:30 AM", "Computer Networks", "Lecturer A"));
//                todaySessions.add(new ClassItem("2", "9:45 AM", "11:15 AM", "Software Engineering", "Lecturer B"));
//                todaySessions.add(new ClassItem("3", "11:30 AM", "1:00 PM", "Computer Architecture", "Lecturer C"));

                // Show the sessions in the recycler view

                break;
        }
    }

}