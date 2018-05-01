package app.heroeswear.com.heroesfb;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by livnatavikasis on 01/05/2018.
 */

public class DataBaseFBManager {
    protected FirebaseDatabase mFirebaseDatabase ;
    DatabaseReference getUsersTokenTable (){
        return mFirebaseDatabase.getReference("usersToken");
    }
}
