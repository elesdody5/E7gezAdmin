package example.com.e7gezadmain.fields;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import example.com.e7gezadmain.R;
import example.com.e7gezadmain.firebase.collection.Stadium;


public class fieldsAdapter extends ArrayAdapter<Stadium> {

    public fieldsAdapter(@NonNull Context context, int resource, @NonNull List<Stadium> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (convertView == null)
            // this converts xml file to java object
            view = LayoutInflater.from(getContext()).inflate(R.layout.field_view, parent, false);
        // to get item from arraylist in position
        Stadium item = getItem(position);
        TextView fieldName_tv = view.findViewById(R.id.field_name);
        fieldName_tv.setText(item.getName());

        TextView address = view.findViewById(R.id.Address_tv);
         address.setText(item.getAddress());


        return view;
    }
}
