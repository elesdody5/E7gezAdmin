package example.com.e7gezadmain.add_field;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import example.com.e7gezadmain.R;
import example.com.e7gezadmain.firebase.collection.Stadium;
import example.com.e7gezadmain.services.Constants;
import example.com.e7gezadmain.services.FetchAddressIntentService;

import static example.com.e7gezadmain.Enum.NAME;
import static example.com.e7gezadmain.Enum.STADIUM;

public class FieldActivity extends AppCompatActivity implements AddressResultReceiver.Receiver {
    private static final int REQUEST_LOCATION = 1;
    private static final String TAG = FieldActivity.class.getName();
    private LocationManager locationManager;
    private AddressResultReceiver resultReceiver;
    public static ArrayList<HashMap<String, String>> notAvailable;
    @BindView(R.id.name)
    TextInputEditText name;
    @BindView(R.id.description)
    TextInputEditText description;
    @BindView(R.id.address)
    TextInputEditText address_tv;
    @BindView(R.id.government_layout)
    TextInputLayout goverLayout;
    @BindView(R.id.government)
    TextInputEditText government;
    @BindView(R.id.city_layout)
    TextInputLayout cityLayout;
    @BindView(R.id.city)
    TextInputEditText city_et;
    @BindView(R.id.area_layout)
    TextInputLayout areaLayout;
    @BindView(R.id.area)
    TextInputEditText area_et;
    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.morning_from_hour)
    TextInputEditText morningFromHour;
    @BindView(R.id.morning_from_spinner)
    Spinner morningFromSpinner;
    @BindView(R.id.to_morning_hour)
    TextInputEditText morningToHour;
    @BindView(R.id.to_morning_spinner)
    Spinner morningToSpinner;
    @BindView(R.id.night_from_hour)
    TextInputEditText nightFromHour;
    @BindView(R.id.night_from_spinner)
    Spinner nightFromSpinner;
    @BindView(R.id.night_to_hour)
    TextInputEditText nightToHour;
    @BindView(R.id.night_to_spinner)
    Spinner nightToSpinner;
    @BindView(R.id.morning_price)
    TextInputEditText morningPrice;
    @BindView(R.id.night_price)
    TextInputEditText nightPrice;
    @BindView(R.id.not_available_container)
    LinearLayout notAvailableContainer;
    @BindView(R.id.from_hour)
    TextInputEditText notAvailableFrom;
    @BindView(R.id.from_spinner)
    Spinner notAvailableFromSpinner;
    @BindView(R.id.to_hour)
    TextInputEditText notAvailableToHour;
    @BindView(R.id.to_spinner)
    Spinner notAvailableToSpinner;
    @BindView(R.id.day)
    Spinner daySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feild);
        ButterKnife.bind(this);
        resultReceiver = new AddressResultReceiver(new Handler(), this);
        notAvailable = new ArrayList<>();
        String[] period = getResources().getStringArray(R.array.period);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, period);
        nightFromSpinner.setAdapter(adapter);
        morningFromSpinner.setAdapter(adapter);
        morningToSpinner.setAdapter(adapter);
        nightToSpinner.setAdapter(adapter);
        notAvailableFromSpinner.setAdapter(adapter);
        notAvailableToSpinner.setAdapter(adapter);

        String[] days = getResources().getStringArray(R.array.days);
        ArrayAdapter<String> day_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, days);
        daySpinner.setAdapter(day_adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(NAME.toString(), name.getText().toString());
        outState.putString("description", description.getText().toString());
    }

    @OnClick(R.id.submit)
    public void submit() {
        if (name.getText().toString().isEmpty()) {
            name.setError("Please enter the name");
            return;
        }
        if (address_tv.getText().toString().isEmpty()) {
            address_tv.setError("Please enter the address");
            return;
        }
        if (government.getText().toString().isEmpty()) {
            government.setError("Please enter the government");
            return;
        }
        if (city_et.getText().toString().isEmpty()) {
            city_et.setError("Please enter the city");
            return;
        }
        if (area_et.getText().toString().isEmpty()) {
            area_et.setError("Please enter the area");
            return;
        }
//        if (morningFromHour.getText().toString().isEmpty()) {
//
//            morningFromHour.setError("Please enter the hour");
//            return;
//        }
//        if (morningToHour.getText().toString().isEmpty()) {
//            morningToHour.setError("Please enter the hour");
//            return;
//        }
//        if (nightFromHour.getText().toString().isEmpty()) {
//            morningFromHour.setError("Please enter the hour");
//            return;
//        }
//        if (nightToHour.getText().toString().isEmpty()) {
//            nightToHour.setError("Please enter the hour");
//            return;
//        }
//        if (morningPrice.getText().toString().isEmpty()) {
//            morningPrice.setError("Please enter the price");
//            return;
//        }
//        if (nightPrice.getText().toString().isEmpty()) {
//            nightPrice.setError("Please enter the price");
//            return;
//        }
        if (notAvailableContainer.getVisibility() == View.VISIBLE) {
            Toast.makeText(this, "please confirm not available first", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i(TAG, morningFromHour.getText().toString());
        addField();
    }

    private void addField() {
        HashMap<String, Long> price = new HashMap<>();
        ArrayList<String> morningList = getmorningList();
        ArrayList<String> nightList = getnightList();
        price.put("morning", Long.parseLong(morningPrice.getText().toString()));
        price.put("night", Long.parseLong(nightPrice.getText().toString()));
        Stadium stadium = new Stadium(name.getText().toString(), description.getText().toString(), address_tv.getText().toString(), price);
        Intent intent = new Intent(this, OwnerActivity.class);
        intent.putExtra(STADIUM.toString(), stadium);
        intent.putExtra("morning", morningList);
        intent.putExtra("night", nightList);
        intent.putExtra("city", city_et.getText().toString());
        intent.putExtra("gov", government.getText().toString());
        intent.putExtra("area", area_et.getText().toString());

        startActivity(intent);

    }

    private ArrayList<String> getnightList() {
        ArrayList<String> list = new ArrayList<>();
        String from = nightFromHour.getText().toString();
        String fromPeriod = nightFromSpinner.getSelectedItem().toString();
        String to = nightToHour.getText().toString();
        String toPeriod = nightToSpinner.getSelectedItem().toString();
        String hour = "";
        Log.i(TAG, hour + from + fromPeriod + to + toPeriod);

        while (!from.equals(to) || !fromPeriod.equals(toPeriod)) {

            Log.i(TAG, hour + from + fromPeriod + to + toPeriod);
            hour = from + " " + fromPeriod;
            from = String.valueOf(Integer.parseInt(from) + 1);
            if (from.equals("12")) {
                fromPeriod = fromPeriod.equals("AM") ? "PM" : "AM";
            }
            if (from.equals("13")) {
                from = "1";
            }
            hour = hour + " " + "-" + " " + from + " " + fromPeriod;
            list.add(hour);

        }
        return list;
    }

    private void getnotAvailableList(String day) {
        notAvailable.clear();
        String from = notAvailableFrom.getText().toString();
        String fromPeriod = notAvailableFromSpinner.getSelectedItem().toString();
        String to = notAvailableToHour.getText().toString();
        String toPeriod = notAvailableToSpinner.getSelectedItem().toString();
        String hour = "";

        while (!from.equals(to) || !fromPeriod.equals(toPeriod)) {
            HashMap<String, String> hourDetails = new HashMap<>();
            Log.i(TAG, hour + from + fromPeriod + to + toPeriod);
            hour = from + " " + fromPeriod;
            from = String.valueOf(Integer.parseInt(from) + 1);
            if (from.equals("12")) {
                fromPeriod = fromPeriod.equals("AM") ? "PM" : "AM";
            }
            if (from.equals("13")) {
                from = "1";
            }
            hour = hour + " " + "-" + " " + from + " " + fromPeriod;
            hourDetails.put("day", day);
            hourDetails.put("hour", hour);
            notAvailable.add(hourDetails);

        }
        Log.i("notAvi", notAvailable.get(0).get("hour") + "");

    }

    private ArrayList<String> getmorningList() {
        ArrayList<String> list = new ArrayList<>();
        String from = morningFromHour.getText().toString();
        String fromPeriod = morningFromSpinner.getSelectedItem().toString();
        String to = morningToHour.getText().toString();
        String toPeriod = morningToSpinner.getSelectedItem().toString();
        String hour = "";
        Log.i(TAG, hour + from + fromPeriod + to + toPeriod);

        while (!from.equals(to) || !fromPeriod.equals(toPeriod)) {

            Log.i(TAG, hour + from + fromPeriod + to + toPeriod);
            hour = from + " " + fromPeriod;
            from = String.valueOf(Integer.parseInt(from) + 1);
            if (from.equals("12")) {
                fromPeriod = fromPeriod.equals("AM") ? "PM" : "AM";
            }
            if (from.equals("13")) {
                from = "1";
            }
            hour = hour + " " + "-" + " " + from + " " + fromPeriod;
            list.add(hour);

        }
        Log.i("hours", list + "");
        return list;
    }

    @OnClick(R.id.notAvailable)
    public void addNotAvailable() {
        notAvailableContainer.setVisibility(View.VISIBLE);
    }

    // to add hour in not available list
    @OnClick(R.id.done)
    public void done() {
        getnotAvailableList(daySpinner.getSelectedItem().toString());
        notAvailableFrom.setText("");
        notAvailableToHour.setText("");
        notAvailableContainer.setVisibility(View.GONE);
        Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();


    }

    @OnClick(R.id.remove)
    public void remove() {
        notAvailableFrom.setText("");
        notAvailableToHour.setText("");
        notAvailableContainer.setVisibility(View.GONE);
        Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show();


    }

    @OnClick(R.id.location)
    public void setLocation() {
        // check permissions
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Location location = getGPSLocation();
            Log.i(TAG, location + toString());
            if (location != null)
                startIntentService(location);
        }
    }

    private Location getGPSLocation() {
        if (ActivityCompat.checkSelfPermission(FieldActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (FieldActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            return null;

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (location != null) {
                return location;

            } else if (location1 != null) {
                return location1;


            } else if (location2 != null) {
                return location2;


            } else {

                Toast.makeText(this, "Unble to Trace your location", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }

    private void buildAlertMessageNoGps() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.confirm))
                .setMessage("Please Turn ON your GPS Connection")
                .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                dialog.cancel();
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void startIntentService(Location lastLocation) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation);
        startService(intent);
    }

    // this method called after convert lat and lag to readable address
    @Override
    public void onReceiveAddress(String gov, String city) {
        goverLayout.setVisibility(View.VISIBLE);
        cityLayout.setVisibility(View.VISIBLE);
        areaLayout.setVisibility(View.VISIBLE);
        government.setText(gov);
        Log.i(TAG, gov);
        city_et.setText(city);

    }
}
