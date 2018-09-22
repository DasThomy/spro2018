package swarm.swarmcomposer.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import swarm.swarmcomposer.R;

/**
 * skippable start Screen with a beautifull and amazing accoustic audio accompanying it
 */
public class NotStupidStartPage extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    Boolean done;
    AnimatorSet animationSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stupid_start_page);

        ImageView imageView = findViewById(R.id.imageViewStartup);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                animationSet.end();
                return false;
            }
        });
        mediaPlayer = MediaPlayer.create(this, R.raw.logotheme);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        int startX = (width - imageView.getWidth()) / 3;
        int startY = (height - imageView.getHeight()) / 3;

        animationSet = new AnimatorSet();
        animationSet.play(ObjectAnimator.ofFloat(imageView, View.X, startX, startX))
                .with(ObjectAnimator.ofFloat(imageView, View.Y, startY, startY))
                .with(ObjectAnimator.ofFloat(imageView, View.SCALE_X, 0.5f, 4f))
                .with(ObjectAnimator.ofFloat(imageView, View.SCALE_Y, 0.5f, 4f));

        animationSet.setDuration(8000);
        animationSet.setInterpolator(new DecelerateInterpolator());
        Main main = new Main(this);
        main.init();
        animationSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mediaPlayer.start();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                Intent intent = new Intent(NotStupidStartPage.this, Login.class);
                startActivity(intent);
                finish();
            }

        });
        animationSet.start();
    }
}
