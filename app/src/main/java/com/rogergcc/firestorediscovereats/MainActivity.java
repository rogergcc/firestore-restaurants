
 package com.rogergcc.firestorediscovereats;

 import android.content.Intent;
 import android.os.Bundle;
 import android.text.Html;
 import android.util.Log;
 import android.view.Menu;
 import android.view.MenuItem;
 import android.view.View;
 import android.widget.Toast;

 import androidx.appcompat.app.AppCompatActivity;
 import androidx.lifecycle.ViewModelProvider;
 import androidx.recyclerview.widget.LinearLayoutManager;

 import com.firebase.ui.auth.AuthUI;
 import com.google.android.material.snackbar.Snackbar;
 import com.google.firebase.auth.FirebaseAuth;
 import com.google.firebase.firestore.CollectionReference;
 import com.google.firebase.firestore.DocumentSnapshot;
 import com.google.firebase.firestore.FirebaseFirestore;
 import com.google.firebase.firestore.FirebaseFirestoreException;
 import com.google.firebase.firestore.Query;
 import com.rogergcc.firestorediscovereats.adapter.RestaurantAdapter;
 import com.rogergcc.firestorediscovereats.databinding.ActivityMainBinding;
 import com.rogergcc.firestorediscovereats.model.Restaurant;
 import com.rogergcc.firestorediscovereats.util.RestaurantUtil;
 import com.rogergcc.firestorediscovereats.viewmodel.MainActivityViewModel;

 import java.util.Collections;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        FilterDialogFragment.FilterListener,
        RestaurantAdapter.OnRestaurantSelectedListener {

    private static final String TAG = "MainActivity";

    private static final int RC_SIGN_IN = 9001;

    private static final int LIMIT = 50;



    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private FilterDialogFragment mFilterDialog;
    private RestaurantAdapter mAdapter;

    private MainActivityViewModel mViewModel;
    private boolean dialogShown;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        
        setSupportActionBar(binding.toolbar);


        binding.filterBar.setOnClickListener(this);
        binding.buttonClearFilter.setOnClickListener(this);

        // View model
        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Initialize Firestore and the main RecyclerView
        initFirestore();

        initRecyclerView();

        // Filter Dialog
        mFilterDialog = new FilterDialogFragment();
    }

    private void initFirestore() {
        // TODO(developer): Implement
        mFirestore = FirebaseFirestore.getInstance();
        // Get the 50 highest rated restaurants
        mQuery = mFirestore.collection("restaurants")
                .orderBy("avgRating", Query.Direction.DESCENDING)
                .limit(LIMIT);

    }

    private void initRecyclerView() {
//        Toast.makeText(this, "init RECYCLWER", Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "MY QUERY"+mQuery, Toast.LENGTH_SHORT).show();
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");

        }

        mAdapter = new RestaurantAdapter(mQuery, this) {

            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    binding.recyclerRestaurants.setVisibility(View.GONE);
                    binding.viewEmpty.setVisibility(View.VISIBLE);
                } else {
                    binding.recyclerRestaurants.setVisibility(View.VISIBLE);
                    binding.viewEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(findViewById(android.R.id.content),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        binding.recyclerRestaurants.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerRestaurants.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Start sign in if necessary
        if (shouldStartSignIn()) {
            startSignIn();
            return;
        }

        // Apply filters
        onFilter(mViewModel.getFilters());

        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    private void onAddItemsClicked() {
        // TODO(developer): Add random restaurants
        CollectionReference restaurants = mFirestore.collection("restaurants");

        for (int i = 0; i < 10; i++) {
            // Get a random Restaurant POJO
            Restaurant restaurant = RestaurantUtil.getRandom(this);

            // Add a new document to the restaurants collection
            restaurants.add(restaurant);
        }
        //showTodoToast();
    }

    @Override
    public void onFilter(Filters filters) {
        // TODO(developer): Construct new query
        //showTodoToast();

        // Construct query basic query
        Query query = mFirestore.collection("restaurants");

        // Category (equality filter)
        if (filters.hasCategory()) {
            query = query.whereEqualTo("category", filters.getCategory());
        }

        // City (equality filter)
        if (filters.hasCity()) {
            query = query.whereEqualTo("city", filters.getCity());
        }

        // Price (equality filter)
        if (filters.hasPrice()) {
            query = query.whereEqualTo("price", filters.getPrice());
        }

        // Sort by (orderBy with direction)
        if (filters.hasSortBy()) {
            query = query.orderBy(filters.getSortBy(), filters.getSortDirection());
        }

        // Limit items
        query = query.limit(LIMIT);

        // Update the query
        mQuery = query;
        mAdapter.setQuery(query);

        // Set header
        binding.textCurrentSearch.setText(Html.fromHtml(filters.getSearchDescription(this)));
        binding.textCurrentSearch.setText(filters.getOrderDescription(this));

        // Save filters
        mViewModel.setFilters(filters);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_items:
                onAddItemsClicked();
                break;
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this);
                startSignIn();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            mViewModel.setIsSigningIn(false);

            if (resultCode != RESULT_OK && shouldStartSignIn()) {
                startSignIn();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filter_bar:
                onFilterClicked();
                break;
            case R.id.button_clear_filter:
                onClearFilterClicked();
        }
    }

    public void onFilterClicked() {
        // Show the dialog containing filter options
//        mFilterDialog.show(getSupportFragmentManager(), FilterDialogFragment.TAG);

        //Toast.makeText(this, "IS ALREADY ADDED ? : "+mFilterDialog.isAdded(), Toast.LENGTH_SHORT).show();

        mFilterDialog.show(getSupportFragmentManager(), FilterDialogFragment.TAG);

//        if (!dialogShown) {
//            dialogShown = true;
//            mFilterDialog.show(getSupportFragmentManager(), FilterDialogFragment.TAG);
//        }

//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        Fragment prev = getSupportFragmentManager().findFragmentByTag("TAG_DIALOG");
//        if (prev == null) {
//            ft.addToBackStack(null);
//            // Create and show the dialog.
//            DialogFragment newFragment = ImageDialog.newInstance(b);
//            newFragment.show(ft, "TAG_DIALOG");
//        }
    }

    public void onClearFilterClicked() {
        mFilterDialog.resetFilters();

        onFilter(Filters.getDefault());
    }

    @Override
    public void onRestaurantSelected(DocumentSnapshot restaurant) {
        // Go to the details page for the selected restaurant
        Intent intent = new Intent(this, RestaurantDetailActivity.class);
        intent.putExtra(RestaurantDetailActivity.KEY_RESTAURANT_ID, restaurant.getId());

        startActivity(intent);
    }

    private boolean shouldStartSignIn() {
        return (!mViewModel.getIsSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    private void startSignIn() {
        // Sign in with FirebaseUI
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
        mViewModel.setIsSigningIn(true);
    }

    private void showTodoToast() {
        Toast.makeText(this, "TODO: Implement", Toast.LENGTH_SHORT).show();
    }
}
