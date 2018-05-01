package app.heroeswear.com.heroesmakers.login.Activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import app.heroeswear.com.common.model.Contact;
import app.heroeswear.com.heroesmakers.R;
import common.BaseActivity;

public class EmergencyContactsActivity extends BaseActivity {
    ArrayList<Contact> mContacts;
    ListView listViewContacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emargency_contacts);

       getContacts();



        ImageButton imageButtonAdd = findViewById(R.id.add);
        listViewContacts = findViewById(R.id.contactList);
        imageButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmergencyContactsActivity.this, NewContactActivity.class);
                startActivityForResult(intent,RESULT_OK);
            }
        });



    }

    private void initContactList(final ArrayList<String> contactsName) {
        if (contactsName != null ) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, contactsName);
            listViewContacts.setAdapter(arrayAdapter);

            listViewContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    // TODO add to the FB
                    String selectedContact = mContacts.get(i).getPhoneNumber();
                    phoneDial(getBaseContext(),selectedContact);
                    Toast.makeText(getApplicationContext(), "Calling " + selectedContact, Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    public static void phoneDial(Context context, String phone) {
        if (phone == null) {
            return;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
    // TODO return emergency mContacts from server or shard preferences
    private ArrayList<String> getContactsNames(ArrayList<Contact> contacts){
        if (contacts != null ) {
            ArrayList<String> arrayList = new ArrayList<>();
            for (Contact contact : contacts) {
                arrayList.add(contact.getName());
            }
            return arrayList;
        }else
            return null;

    }

    // TODO get contact from server
    private void getContacts(){
        ArrayList<Contact> arrayList;
        getFbManager().getContactsRefList().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Contact c = dataSnapshot.getValue(Contact.class);
                ArrayList<Contact> arrayList = new ArrayList<>();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Contact contact = dsp.getValue(Contact.class);
                    arrayList.add(contact);
                }
                mContacts = arrayList;
                final ArrayList<String> contactsName = getContactsNames(mContacts);
                initContactList(contactsName);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            getContacts();
        }
    }
}