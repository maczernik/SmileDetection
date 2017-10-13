package salsa.mac.smile_detection;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

/**
 * Glowna klasa obslugujące pierwsze wywolanie programu. Uruchamia kolejne aktywności. Zajmuje się
 * obslugą menu i przesyłem wybranych parametrow do poszczegolnych aktywności.
 *
 * @autor Maciej Czernik
 * @version 1
 */
public class MainActivity extends AppCompatActivity {
    static final int PICK_CONTACT_REQUEST = 1;
    public String settingFaceSize;
    public String settingFaceScale;
    public String settingFaceNei;
    public String settingSmileSize;
    public String settingSmileScale;
    public String settingSmileNei;
    public Locale myLocale;

    /**
     * Metoda uruchomiona przy starcie aktywności. Inicjalizuje wartości początkowe.
     *
     * @param savedInstanceState
     * @autor Maciej Czernik
     * @version 1
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myLocale = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String intent = this.getIntent().getStringExtra("language");
        setDefaultSetting();
        setActionToButtons();
    }

    /**
     * Metoda wywoływana podczas tworzenia menu.
     *
     * @param menu Menu
     * @autor Maciej Czernik
     * @version 1
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;

    }
    /**
     * Metoda przypisująca akcje dla kolejnych elementów znajdujących się w menu.
     *
     * @autor Maciej Czernik
     * @version 1
     * @param item Elemeny menu.
     * @return True dla prawidłowego przypisania wartości, false dla błędnego.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.rattio_pl:
                setLocale("pl");
                break;
            case R.id.rattio_eng:
                setLocale("en");
                break;
            case R.id.aboutme:
                Intent intent = new Intent(MainActivity.this, AboutMeActivity.class);
                startActivityForResult(intent, PICK_CONTACT_REQUEST);
                break;
            case R.id.help:
                Toast.makeText(getBaseContext(),getString(R.string.helpInfo) , Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_size5:
                settingFaceSize = "5";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.menu_size20:
                settingFaceSize = "20";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.menu_size30:
                settingFaceSize = "30";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.menu_size40:
                settingFaceSize = "40";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.menu_size50:
                settingFaceSize = "50";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;

            case R.id.scale_11:
                settingFaceScale = "1.1";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.scale_15:
                settingFaceScale = "1.5";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.scale_20:
                settingFaceScale = "2.0";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.scale_40:
                settingFaceScale = "4.0";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.face_nei0:
                settingFaceNei = "0";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.face_nei1:
                settingFaceNei = "1";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.face_nei2:
                settingFaceNei = "2";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.face_nei5:
                settingFaceNei = "50";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.face_nei10:
                settingFaceNei = "100";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.menu_sizeSmile5:
                settingSmileSize = "5";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.menu_sizeSmile20:
                settingSmileSize = "20";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.menu_sizeSmile30:
                settingSmileSize = "30";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.menu_sizeSmile40:
                settingSmileSize = "40";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.menu_sizeSmile50:
                settingSmileSize = "50";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;

            case R.id.scale_Smile11:
                settingSmileScale = "1.1";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.scale_Smile15:
                settingSmileScale = "1.5";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.scale_Smile20:
                settingSmileScale = "2.0";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.scale_Smile40:
                settingSmileScale = "4.0";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.face_neiSmile0:
                settingSmileNei = "0";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.face_neiSmile1:
                settingSmileNei = "1";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.face_neiSmile2:
                settingSmileNei = "2";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.face_neiSmile5:
                settingSmileNei = "50";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            case R.id.face_neiSmile10:
                settingSmileNei = "100";
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
        }
        return true;
    }

    /**
     * Metoda ustawiająca akcję dla przycisków w aktywności.
     *
     * @autor Maciej Czernik
     * @version 1
     */
    public void setActionToButtons(){
        Button clickButton = (Button) findViewById(R.id.runSmileDetect);
        clickButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RealDetectionActivity.class);

                intent.setPackage("org.opencv.engine");
                intent.putExtra("setting_faceSize",settingFaceSize);
                intent.putExtra("setting_faceScale",settingFaceScale);
                intent.putExtra("setting_faceNei",settingFaceNei);
                intent.putExtra("setting_smileSize",settingSmileSize);
                intent.putExtra("setting_smileScale",settingSmileScale);
                intent.putExtra("setting_smileNei", settingSmileNei);
                startActivity(intent);
            }
        });
        Button clickButton1 = (Button) findViewById(R.id.runTimer);
        clickButton1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TimerActivity.class);
                    intent.putExtra("setting_faceSize",settingFaceSize);
                    intent.putExtra("setting_faceScale",settingFaceScale);
                    intent.putExtra("setting_faceNei",settingFaceNei);
                    intent.putExtra("setting_smileSize",settingSmileSize);
                    intent.putExtra("setting_smileScale",settingSmileScale);
                    intent.putExtra("setting_smileNei", settingSmileNei);
                    startActivity(intent);


            }
        });
        Button clickButton3 = (Button) findViewById(R.id.btn_pick_hPhoto);
        clickButton3.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HappyPhotoActivity.class);
                intent.putExtra("setting_faceSize",settingFaceSize);
                intent.putExtra("setting_faceScale",settingFaceScale);
                intent.putExtra("setting_faceNei",settingFaceNei);
                intent.putExtra("setting_smileSize",settingSmileSize);
                intent.putExtra("setting_smileScale",settingSmileScale);
                intent.putExtra("setting_smileNei", settingSmileNei);
                startActivity(intent);
            }
        });
        Button clickButton4 = (Button) findViewById(R.id.btn_happyTime);
        clickButton4.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TenSecActivity.class);
                intent.putExtra("setting_faceSize",settingFaceSize);
                intent.putExtra("setting_faceScale",settingFaceScale);
                intent.putExtra("setting_faceNei",settingFaceNei);
                intent.putExtra("setting_smileSize",settingSmileSize);
                intent.putExtra("setting_smileScale",settingSmileScale);
                intent.putExtra("setting_smileNei", settingSmileNei);
                startActivity(intent);
            }
        });
    }

    /**
     * Metoda zmienia język w aktywności i wszyskich kolejnych aktywności.
     *
     * @param lang Wybrany język. Dostępne wartości 'en' i 'pl'.
     * @autor Maciej Czernik
     * @version 1
     */
    public void setLocale(String lang) {
        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, MainActivity.class);
        refresh.putExtra("language",lang);
        startActivity(refresh);
    }

    /**
     * Metoda ustawia wartości domyślne dla parametrów detekcji twarzy i uśmiechu.
     *
     * @autor Maciej Czernik
     * @version 1
     */
    public void setDefaultSetting(){
         settingFaceSize = "20";
         settingFaceScale = "1.1";
         settingFaceNei = "2";
         settingSmileSize = "10";
         settingSmileScale = "1.1";
         settingSmileNei = "0";
    }
}
