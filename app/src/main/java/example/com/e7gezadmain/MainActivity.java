package example.com.e7gezadmain;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import example.com.e7gezadmain.add_field.FieldActivity;
import example.com.e7gezadmain.fields.FieldsListActivity;
import example.com.e7gezadmain.firebase.AppDatabase;

public class MainActivity extends AppCompatActivity {
    private ArrayAdapter<String> mGovernmentsadapter;
    private ArrayList<String> governmentsList;
    private ArrayAdapter<String> mCityAdapter;
    private AreasAdapter areasAdapter;
    private ArrayList<String> mCityList;
    private ArrayList<String> mAreasList;
    @BindView(R.id.city_list)
    ListView areasListView;
    @BindView(R.id.better_spinner)
    MaterialBetterSpinner governmentSpinner;
    @BindView(R.id.citySpinner)
    MaterialBetterSpinner citySpinner;
    @BindView(R.id.loading)
    TextView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if(savedInstanceState==null) {
            // initialize database
            AppDatabase database = new AppDatabase();
        }
        governmentsList = new ArrayList<>();
        mCityList = new ArrayList<>();
        mAreasList = new ArrayList<>();
        mGovernmentsadapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());

        governmentSpinner.setAdapter(mGovernmentsadapter);
        mCityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        citySpinner.setAdapter(mCityAdapter);

        areasAdapter = new AreasAdapter(this, R.layout.city_view, new ArrayList<String>());
        areasListView.setAdapter(areasAdapter);
        areasListView.setOnItemClickListener((parent, view, position, id) -> {
            String city = areasAdapter.getItem(position);
            Intent intent = new Intent(this, FieldsListActivity.class);
            intent.putExtra(Enum.AREA.toString(), city);
            startActivity(intent);
        });
        governmentSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String government = mGovernmentsadapter.getItem(position);
            AppDatabase.setGovernmentDocumentReference(government);
            fetchCities(government);
        });
        citySpinner.setOnItemClickListener((parent, view, position, id) -> {
            String city = mCityAdapter.getItem(position);
            AppDatabase.setCityDocument(city);
            fetchAreas(city);
        });
        if (savedInstanceState == null)
            fetchGovernments();
    }
    private void fetchAreas(String city) {
        mCityList.clear();
        loading.setVisibility(View.VISIBLE);
        // to get all locations from Firebase
        AppDatabase.getCityDocument().collection("areas").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            mAreasList.add(document.getId());

                        }
                        if (!mAreasList.isEmpty()) {
                            areasAdapter.clear();
                            areasAdapter.addAll(mAreasList);
                            loading.setVisibility(View.INVISIBLE);
                        } else {
                            loading.setText(getResources().getString(R.string.no_Areas));
                        }

                    }
                });
    }

    private void fetchCities(final String government) {
        mCityList.clear();
        loading.setVisibility(View.VISIBLE);
        // to get all locations from Firebase
        AppDatabase.getGovernmentsCollection().document(government).collection("cities").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            mCityList.add(document.getId());

                        }
                        if (!mCityList.isEmpty()) {
                            mCityAdapter.clear();
                            mCityAdapter.addAll(mCityList);
                            loading.setVisibility(View.INVISIBLE);
                        } else {
                            loading.setText(getResources().getString(R.string.no_Areas));
                        }

                    }
                });
    }


    // method to get all locations from server
    private void fetchGovernments() {
        // to check the internet connection before fetch data
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // to get the statue of network
        NetworkInfo networkInfo = (cm).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            governmentsList.clear();
            // to get all governments from Firebase
            AppDatabase.getGovernmentsCollection().get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                governmentsList.add(document.getId());

                            }
                            if (!governmentsList.isEmpty()) {
                                mGovernmentsadapter.clear();
                                mGovernmentsadapter.addAll(governmentsList);
                                governmentSpinner.setVisibility(View.VISIBLE);
                                citySpinner.setVisibility(View.VISIBLE);
                                loading.setVisibility(View.GONE);
                            }
                        }
                    });
        } else
            loading.setText(getResources().getString(R.string.no_internet_connection));
    }
    @OnClick(R.id.add)
    public void add()
    {
        startActivity(new Intent(this, FieldActivity.class));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("spinnerVisibility", governmentSpinner.getVisibility());
        outState.putInt("loadingVisibility", loading.getVisibility());
        outState.putStringArrayList("locationList", governmentsList);
        outState.putStringArrayList("cityList", mCityList);
        outState.putStringArrayList("areaList", mAreasList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        governmentSpinner.setVisibility(savedInstanceState.getInt("spinnerVisibility"));
        citySpinner.setVisibility(savedInstanceState.getInt("spinnerVisibility"));
        loading.setVisibility(savedInstanceState.getInt("loadingVisibility"));
        governmentsList = savedInstanceState.getStringArrayList("locationList");
        mCityList = savedInstanceState.getStringArrayList("cityList");
        mAreasList = savedInstanceState.getStringArrayList("areaList");
        mGovernmentsadapter.clear();
        mGovernmentsadapter.addAll(governmentsList);
        mCityAdapter.clear();
        mCityAdapter.addAll(mCityList);
        areasAdapter.clear();
        areasAdapter.addAll(mAreasList);
    }

}
