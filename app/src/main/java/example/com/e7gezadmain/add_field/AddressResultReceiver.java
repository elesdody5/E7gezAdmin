package example.com.e7gezadmain.add_field;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;


import example.com.e7gezadmain.services.Constants;

class AddressResultReceiver extends ResultReceiver {
    private Receiver mReceiver;
    public AddressResultReceiver(Handler handler, Receiver mReceiver) {
        super(handler);
        this.mReceiver=mReceiver;
    }
    public interface Receiver {
        public void onReceiveAddress(String gov, String city);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        if (resultData == null) {
            return;
        }

        // Display the address string
        // or an error message sent from the intent service.
        String area = resultData.getString(Constants.RESULT_DATA_KEY);
        String gov = resultData.getString(Constants.RESULT_DATA_KEY+"gov");
        String city = resultData.getString(Constants.RESULT_DATA_KEY+"city");
        if (area == null) {
            area = "";
        }
        mReceiver.onReceiveAddress(gov,city);
//        // Show a toast message if an address was found.
//        if (resultCode == Constants.SUCCESS_RESULT) {
//            showToast(getString(R.string.address_found));
//        }

    }
}

