package sunshineapp.example.com.sunshineapp;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {

    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
    String mIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Button button = (Button)findViewById(R.id.testing_explicit_intents);
        Intent getActivityWhichStartedIntent = getIntent();
        if(getActivityWhichStartedIntent != null){
            mIntent = getActivityWhichStartedIntent.getStringExtra("WeatherForToday");
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.testing_explicit_intents){
                    Toast.makeText(DetailActivity.this,mIntent,Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.detail,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.action_share:
                    menuItem.setIntent(shareWeatherDetails(mIntent+FORECAST_SHARE_HASHTAG));
                    return true;
            case R.id.settings:
                  startActivity(new Intent(DetailActivity.this, SettingsActivity.class));
                  return true;
            case android.R.id.home:
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private Intent shareWeatherDetails(String weather) {

        String mimeType = "text/plain";
        String title = "Share Weather";
        String textToShare = weather;

      Intent shareIntent =  ShareCompat.IntentBuilder.from(this).
                setType(mimeType).setText(textToShare).getIntent();
        return  shareIntent;
    }
}
