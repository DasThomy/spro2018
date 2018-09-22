package swarm.swarmcomposer.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import swarm.swarmcomposer.helper.CallBack;
import swarm.swarmcomposer.R;

public class Login extends AppCompatActivity implements CallBack {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        this.getWindow().setBackgroundDrawableResource(R.drawable.background);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkDiskPermission();
        EditText editTextEmail = findViewById(R.id.editTextEmail);
        editTextEmail.setText(AppInstance.getInstance().getData().getEmail());
        final Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setTextSize(AppInstance.getInstance().getData().getFontSizeNormalText());
        Button buttonComb = findViewById(R.id.buttonShowCombination);
        buttonComb.setTextSize(AppInstance.getInstance().getData().getFontSizeNormalText());
        Button buttonProd = findViewById(R.id.buttonShowProducts);
        buttonProd.setTextSize(AppInstance.getInstance().getData().getFontSizeNormalText());

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextEmail = findViewById(R.id.editTextEmail);
                AppInstance.getInstance().getData().setEmail(editTextEmail.getText().toString());


                EditText editTextPassword = findViewById(R.id.editTextPassword);
                if (!editTextEmail.getText().toString().contains("@") || !editTextEmail.getText().toString().contains(".")) {
                    callBack(2);
                    return;
                }
                AppInstance.getInstance().getData().setEmail(editTextEmail.getText().toString());
                AppInstance.getInstance().saveLokalData(Login.this);
                AppInstance.getInstance().getClient().setPassword(editTextPassword.getText().toString());
                AppInstance.getInstance().getClient().login(Login.this);


            }
        });

        buttonComb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ListCombination.class);
                startActivity(intent);
            }
        });

        buttonProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ListProducts.class);
                startActivity(intent);
            }
        });

        EditText loginStuff = findViewById(R.id.editTextPassword);

        loginStuff.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        EditText editTextEmail = findViewById(R.id.editTextEmail);
                        EditText editTextPassword = findViewById(R.id.editTextPassword);
                        if (!editTextEmail.getText().toString().contains("@") || !editTextEmail.getText().toString().contains(".")) {
                            callBack(2);
                            return false;
                        }
                        AppInstance.getInstance().getData().setEmail(editTextEmail.getText().toString());
                        AppInstance.getInstance().saveLokalData(Login.this);
                        AppInstance.getInstance().getClient().setPassword(editTextPassword.getText().toString());
                        AppInstance.getInstance().getClient().login(Login.this);
                        return true;
                    }
                return false;
            }
        });


    }


    @Override
    public void callBack(int i) {
        switch (i) {
            case 0://Alles ok
                Intent intent = new Intent(Login.this, ListCombination.class);
                startActivity(intent);
                Toast.makeText(this, R.string.LoginCor, Toast.LENGTH_LONG).show();
                break;
            case 1://Fehler
                Toast.makeText(this, R.string.LogInWrong, Toast.LENGTH_LONG).show();
                break;
            case 2:
                Toast.makeText(this, R.string.emailWrong, Toast.LENGTH_LONG).show();
            case 99:

                Toast.makeText(this, R.string.networkError, Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }

    private void checkDiskPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
    }
}
