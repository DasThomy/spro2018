package swarm.swarmcomposer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public abstract class ListStuff extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
    }

    protected abstract int getLayoutResourceId();

}
