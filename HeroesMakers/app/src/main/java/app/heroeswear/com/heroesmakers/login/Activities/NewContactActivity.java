package app.heroeswear.com.heroesmakers.login.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import app.heroeswear.com.common.model.Contact;
import app.heroeswear.com.heroesmakers.R;
import common.BaseActivity;

public class NewContactActivity extends BaseActivity {

    private Contact contact = new Contact();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        final EditText editTextName = findViewById(R.id.name);
        final EditText editTextPhoneNumber = findViewById(R.id.phone_number);
        Button buttonAdd = findViewById(R.id.add_button);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contact.setName(editTextName.getText().toString());
                contact.setPhoneNumber(editTextPhoneNumber.getText().toString());
                updateEmergancyContactListAndGoToPreviusActivity(contact);
            }
        });

    }

    // TODO from what activity i got here? and what else need to do before going back
    private void updateEmergancyContactListAndGoToPreviusActivity(Contact contact){
        getFbManager().updateNewContact(contact);
        finishActivity(RESULT_OK);

        Intent intent = new Intent(NewContactActivity.this, EmergencyContactsActivity.class);
        startActivity(intent);
    }
}