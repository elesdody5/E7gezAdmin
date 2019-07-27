package example.com.e7gezadmain.firebase;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public  class AppDatabase  {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static FirebaseFirestore sInstance ;
    private static CollectionReference governmentsCollection;
    private static DocumentReference governmentDocument;
    private static DocumentReference cityDocument;
    private static CollectionReference stadiumCollection;
    private static DocumentReference stadiumDocument;
    private static CollectionReference userCollectionReference;
    private static DocumentReference userDocument;
    private static CollectionReference ownerCollection;
    private static DocumentReference ownerDocument;

    public static void setCityDocument(String city) {
        cityDocument = governmentDocument.collection("cities").document(city);
    }

    public static DocumentReference getCityDocument() {
        return cityDocument;
    }

    public  DocumentReference getGovernmentDocument() {
        return governmentDocument;
    }

    public static void setGovernmentDocumentReference(String location) {
        governmentDocument = getGovernmentsCollection().document(location);
    }

    public  AppDatabase () {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = FirebaseFirestore.getInstance();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
    }

    public static CollectionReference getStadiumCollection() {
        if (stadiumCollection==null)
        stadiumCollection=sInstance.collection("stadiums");
        return stadiumCollection;
    }
    public static CollectionReference getOwnerCollection() {
        if (ownerCollection==null)
            ownerCollection=sInstance.collection("owners");
        return ownerCollection;
    }

    public static CollectionReference getGovernmentsCollection() {
        if(governmentsCollection==null)
            governmentsCollection = sInstance.collection("governments");
        return governmentsCollection;
    }
    public static CollectionReference getUserCollectionReference() {
        if(userCollectionReference==null)
            userCollectionReference = sInstance.collection("users");
        return userCollectionReference;
    }


    public static DocumentReference getUserDocument(String id) {
       return userDocument = getUserCollectionReference().document(id);
    }
    public static DocumentReference getOwnerDocument(String id)
    {
        return ownerDocument = getOwnerCollection().document(id);
    }

    public static DocumentReference getStadiumDocument() {
        return stadiumDocument;
    }

    public static void setStadiumDocument(String id) {

        AppDatabase.stadiumDocument = getStadiumCollection().document(id);
    }
}
