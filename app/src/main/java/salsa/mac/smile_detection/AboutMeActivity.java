package salsa.mac.smile_detection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Klasa jest odpowiedzialna za aktywność wyświetlającą inforamcje o aplikacji.
 *
 * @autor Maciej Czernik
 * @version 1
 */
public class AboutMeActivity extends AppCompatActivity {

    /**
     *  Metoda wywoływana przy starcie aktywności, łączy ciągni znaków i wstawia uzyskany ciąg do
     *  TextViev.
     *
     * @autor Maciej Czernik
     * @version 1
     * @param savedInstanceState param
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);
        TextView tv = (TextView) findViewById(R.id.aboutMe_textView);
        String text = "";
        text += "                  Maciej Czernik"+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        text += "                       POLSL"+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        text += getString(R.string.title)+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        text += ""+System.getProperty("line.separator");
        tv.setText(text);
    }
}
