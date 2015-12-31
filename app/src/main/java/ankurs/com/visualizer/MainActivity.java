package ankurs.com.visualizer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    public static final String COLOR_INTENT = "cube26.musicplayer.intentcolor";
    public static final String COLOR_VALUE = "cube26.musicplayer.colorname";
    public static final String PLAY_INTENT = "cube26.musicplayer.intentplaypause";
    public static final String PLAY_VALUE = "cube26.musicplayer.pauseaction";

    private static final int MIN = 50;
    private static final long TIME_INTERVAL = 50;

    private Visualizer visu;

    private Visualizer.OnDataCaptureListener capturelistener;
    static long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (null != visu) {
            visu.setEnabled(false);
            visu.release();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public static int getRandom(int max, int min) {
        if (max > 255){
            max = 255;
        }
        if (min < 0){
            min = 0;
        }
        if (max < min){
            max = 255;
        }
        Double val = Math.random()*max+min;
        return val.intValue();
    }

    public static int getRandomColor(int power) {
        return Color.argb(255, MainActivity.getRandom(power, MIN),
                MainActivity.getRandom(power,MIN), MainActivity.getRandom(power,MIN));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.sendColor();
        return true;
    }

    public void sendColor() {
        final Context context = this.getApplicationContext();
        capturelistener = new Visualizer.OnDataCaptureListener(){
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                              int samplingRate)
            {
                if ((time + TIME_INTERVAL) < System.currentTimeMillis())
                {
                    time = System.currentTimeMillis();
                }
                else
                {
                    return;
                }
                Log.d("Ankur", "sampling rate is " + samplingRate);
                //FIXME this is some random algo make it proper
                int value1 = 0;
                for (int i=0; i< 50; i++)
                {
                    value1 += bytes[i];
                }
                value1 = value1 / 50;

                int value2 = 0;
                for (int i=50; i< 100; i++)
                {
                    value2 += bytes[i];
                }
                value2 = value2 / 50;

                int value3 = 0;
                for (int i=100; i< 150; i++)
                {
                    value3 += bytes[i];
                }
                value3 = value3 / 50;

                int value4 = 0;
                for (int i=150; i< 200; i++)
                {
                    value4 += bytes[i];
                }
                value4 = value4 / 50;

                int value = (int) ((0.4*value1) + (0.3*value2) +(0.2*value3) +(0.2*value4));
                if (value < 0){
                    value = -value;
                }
                Intent in = new Intent();
                in.setAction(PLAY_INTENT);
                in.putExtra(PLAY_VALUE, false);
                Log.i("Ankur", "sending play intent");
                context.sendBroadcast(in);
                Log.i("Ankur", "intensity is " + value);
                in = new Intent();
                in.setAction(COLOR_INTENT);
                in.putExtra(COLOR_VALUE, MainActivity.getRandomColor(value));
                Log.i("Ankur", "sending intent " + in.getIntExtra(COLOR_VALUE, 0));
                context.sendBroadcast(in);
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] bytes,
                                         int samplingRate)
            {
                // do nothing
            }
        };
        visu = new Visualizer(0);
        visu.setDataCaptureListener(capturelistener, Visualizer.getMaxCaptureRate()/2, true, true);
        visu.setEnabled(true);
        Log.d("ANKUR", "visulization enabled");
    }
}
