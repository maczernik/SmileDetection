package salsa.mac.smile_detection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import java.io.File;
import java.io.InputStream;
import java.util.Calendar;

/**
 * Klasa odpowiedzialna za modul Szczesliwego zdjecia.
 *
 * @autor Maciej Czernik
 * @version 1
 */
public class HappyPhotoActivity extends Activity {
    /** Przycisk otwierajacy modul do pobrania zdjecia*/
    Button btn_pickPhoto;
    /** Przycisk zapisujacy usmiechy ze zdjecia*/
    Button btn_saveSmilesFromPhoto;
    /** Przycisk zaznaczajacy usmiechy na zdjeciu*/
    Button btn_getSmilesFromPhoto;
    /** Sciezka do katalogu z zapisanymi usmiechami*/
    String dir = "/media/smiledetection/";
    /** Sugerowana wielkosci twarzy na obrazie */
    private String                 setting_faceSize;
    /** Parametr skali twarzy wykorzystany w detectMultiScale podczas detekcji twarzy*/
    private Double                 setting_faceScale;
    /** Parametr określajacy dokładonosc detectMultiScale podczas detekcji twarzy*/
    private int                    setting_faceNei;
    /** Sugerowana wielkosc usmiechu na obrazie twarzy*/
    private String                 setting_smileSize;
    /** Parametr skali twarzy wykorzystany w detectMultiScale podczas detekcji usmiechu*/
    private Double                 setting_smileScale;
    /** Parametr określajacy dokładonosc detectMultiScale podczas detekcji usmiechu*/
    private int                    setting_smileNei;
    /** Miejsce z podgladem zdjecia*/
    ImageView iv1;
    /**
     *
     */
    private static Context context;
    /**
     * Wybor kamery: przednia, tylna
     * @autor Maciej Czernik
     * @version 1
     */
    static final int CAM_REQUEST = 0;
    /**
     * Metoda ładująca openCV i inicjalizująca klasyfikatory.
     * @autor Maciej Czernik
     * @version 1
     */
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    /**
     * Metoda odczytująca przesłane parametry ustawień detekcji usmiechu i twarzy. Ustawia domyslne
     * jezeli parametry nie zostaly przeslane.
     * @autor Maciej Czernik
     * @version 1
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_happy_photo);
        setComponents();
        context = getApplicationContext();
        iv1.setImageDrawable(Drawable.createFromPath(Environment.getExternalStorageDirectory() + dir + "2.jpg"));
        OpenCVLoader.initOpenCV(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mLoaderCallback);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if(extras.getString("setting_faceSize")!= null) setting_faceSize =  extras.getString("setting_faceSize");
            else setting_faceSize = "30";
            if(extras.getString("setting_faceScale")!= null) setting_faceScale = Double.parseDouble(extras.getString("setting_faceScale"));
            else setting_faceScale = 1.1;
            if(extras.getString("setting_faceNei")!= null) setting_faceNei = Integer.parseInt(extras.getString("setting_faceNei"));
            else setting_faceNei = 2;
            if(extras.getString("setting_smileSize")!= null) setting_smileSize =  extras.getString("setting_smileSize");
            else setting_smileSize = "10";
            if(extras.getString("setting_smileScale")!= null) setting_smileScale = Double.parseDouble(extras.getString("setting_smileScale"));
            else setting_smileScale = 1.1;
            if(extras.getString("setting_smileNei")!= null) setting_smileNei = Integer.parseInt(extras.getString("setting_smileNei"));
            else setting_smileNei = 0;
        }else{
            setting_faceSize = "20";
            setting_faceScale = 1.1;
            setting_faceNei = 2;
            setting_smileSize = "5";
            setting_smileScale = 1.1;
            setting_smileNei = 0;
        }
    }

    /**
     * Ustawia akcje dla przyciskow. Wywołuje modul MediaStore w celu zrobienia zdjecia Dokonuje
     * detekcji usmiechu z klasy SmileDetection i zapisuje we pliku 2.jpg oraz zapisuje wykryte
     * usmiechniete twarze w pliku.
     * @autor Maciej Czernik
     * @version 1
     */
    public void setComponents() {
        btn_pickPhoto = (Button) findViewById(R.id.btn_pickPhoto);
        btn_getSmilesFromPhoto = (Button) findViewById(R.id.btn_getSmileFromPhoto);
        btn_saveSmilesFromPhoto = (Button) findViewById(R.id.btn_saveSmileFromPhoto);
        iv1 = (ImageView) findViewById(R.id.imageView);
        btn_pickPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = getFile();
                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(camera_intent, CAM_REQUEST);
            }
        });
        btn_getSmilesFromPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                InputStream is1 = getResources().openRawResource(R.raw.haarcascade_smile);
                File cascadeFaceDir = getDir("cascadeFace", Context.MODE_PRIVATE);
                File cascadeSmileDir = getDir("cascadeSmile", Context.MODE_PRIVATE);
                SmileDetection SDObject = new SmileDetection(is, is1, cascadeFaceDir, cascadeSmileDir);
                SDObject.setSettings(setting_faceSize,setting_faceScale,setting_faceNei,setting_smileSize,setting_smileScale,setting_smileNei);
                SDObject.setSmilesOnPhoto(Environment.getExternalStorageDirectory() + dir);
                iv1.setImageDrawable(Drawable.createFromPath(Environment.getExternalStorageDirectory() + dir+"2.jpg"));
            }
        });
        btn_saveSmilesFromPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                InputStream is1 = getResources().openRawResource(R.raw.haarcascade_smile);
                File cascadeFaceDir = getDir("cascadeFace", Context.MODE_PRIVATE);
                File cascadeSmileDir = getDir("cascadeSmile", Context.MODE_PRIVATE);
                SmileDetection SDObject = new SmileDetection(is, is1, cascadeFaceDir, cascadeSmileDir);
                SDObject.setSettings(setting_faceSize,setting_faceScale,setting_faceNei,setting_smileSize,setting_smileScale,setting_smileNei);
                SDObject.saveSmilesOnPhoto(Environment.getExternalStorageDirectory() + dir);
            }
        });
    }
    private String getFilePath(){

    return Environment.getExternalStorageDirectory() + dir+"1.jpg";
    }

    /**
     * Zwraca sciezke do pliku z zapisanym zdjeciem, w razie braku tworzy odpowiedni folder.
     * @autor Maciej Czernik
     * @version 1
     * @return Zwraca sciezkę do pliku z zapisanym zdjeciem na ktorym dokonywana jest detekcja usmiechu
     */
    private File getFile(){
        File folder = new File(Environment.getExternalStorageDirectory() + dir);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        File image_folder = new File(folder,"1.jpg");

        return image_folder;
    }

    /**
     * Ustawia zdjecie w momencie wykonania aktywnosci.
     * See <a href="http://developer.android.com/reference/android/app/Activity.html">Activity</a>
     * @param requestCode
     * @param resultCode
     * @param data
     * @autor Maciej Czernik
     * @version 1
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        String path = Environment.getExternalStorageDirectory() + dir+"1.jpg";
        iv1.setImageDrawable(Drawable.createFromPath(path));

    }

}
