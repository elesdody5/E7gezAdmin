package example.com.e7gezadmain.fields;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.e7gezadmain.R;
import example.com.e7gezadmain.firebase.AppDatabase;
import example.com.e7gezadmain.firebase.collection.Stadium;

import static example.com.e7gezadmain.Enum.AREA;

public class FieldsListActivity extends AppCompatActivity {
    private NetworkInfo networkInfo;
    private ConnectivityManager cm;
    @BindView(R.id.fields_list)
    ListView fields_listView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.empty_text)
    TextView emptyView;
    private AppDatabase database;
    private ArrayList<Stadium> stadiumsList;
    private fieldsAdapter adapter;

    private String area;
    private DocumentReference areaDocumentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fields);
        ButterKnife.bind(this);
        // to check the internet connection before fetch data
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // to get the statue of network
        networkInfo = (cm).getActiveNetworkInfo();
        stadiumsList = new ArrayList<>();
        adapter = new fieldsAdapter(this, R.layout.field_view, new ArrayList<Stadium>());
        fields_listView.setAdapter(adapter);
        if ( getIntent().hasExtra(AREA.toString())) {
            String area = getIntent().getStringExtra(AREA.toString());
            fields_listView.setEmptyView(emptyView);
            fetchStadiumsByarea(area);
        }
    }
    private void fetchStadiumsByarea(String area) {
        progressBar.setVisibility(View.VISIBLE);
        if (networkInfo != null && networkInfo.isConnected()) {
            stadiumsList.clear();
            areaDocumentReference = AppDatabase.getCityDocument().collection("areas").document(area);
            areaDocumentReference.collection("stadiums").get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentReference mStadDocumentReference;
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                mStadDocumentReference = document.getDocumentReference("stadium");

                                final DocumentReference finalMStadDocumentReference = mStadDocumentReference;
                                mStadDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        adapter.add(new Stadium(finalMStadDocumentReference.getId(),
                                                documentSnapshot.getString("name")
                                                ,  documentSnapshot.getString("address"),
                                                (HashMap<String, Long>) documentSnapshot.get("price"),
                                                documentSnapshot.getString("image_path")));
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                });







                            }
                        }
                    });
        }

    }
}
