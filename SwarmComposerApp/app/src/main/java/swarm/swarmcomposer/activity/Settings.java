package swarm.swarmcomposer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import swarm.swarmcomposer.R;

/**
 * Settings Page
 * This Page is used to:
 * change the language
 * change the serveradresse
 * and logout.
 */
public class Settings extends AppCompatActivity {
    TextView accountName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button buttonLogout = findViewById(R.id.buttonSettingsLogout);
        Button buttonSave = findViewById(R.id.buttonSettingsSave);
        EditText editTextServerAdresse = findViewById(R.id.editTextIPAdresse);
        accountName = findViewById(R.id.accountName);
        if (AppInstance.getInstance().getData().getLoggedIn()) {
            accountName.setText(AppInstance.getInstance().getData().getEmail());
        } else accountName.setText("");

        editTextServerAdresse.setText(AppInstance.getInstance().getData().getServerAddresse());
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppInstance.getInstance().getClient().logout();

                Intent intent = new Intent(Settings.this, Login.class);
                startActivity(intent);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextServerAdresse = findViewById(R.id.editTextIPAdresse);
                AppInstance.getInstance().getData().setServerAddresse(editTextServerAdresse.getText().toString());
                AppInstance.getInstance().saveLokalData(Settings.this);
                AppInstance.getInstance().getClient().setup(Settings.this);
                //TODO Language

            }
        });
    }
}
