package example.com.e7gezadmain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;


class AreasAdapter extends ArrayAdapter<String> {


    public AreasAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (convertView == null)
            // this converts xml file to java object
            view = LayoutInflater.from(getContext()).inflate(R.layout.city_view, parent, false);
        // to get item from arraylist in position
        String item = getItem(position);
        TextView city_tv = view.findViewById(R.id.city_tv);
        city_tv.setText(item);


        return view;
    }
}
