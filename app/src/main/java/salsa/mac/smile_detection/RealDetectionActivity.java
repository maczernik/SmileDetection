package salsa.mac.smile_detection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Range;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.Objdetect;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.objdetect.CascadeClassifier;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

/**
 * Klasa odpowiedzialna za aktywnosc detekcji uśmiechu na obrazie dostarczonym z kamery tylniej urzadzenia
 * i wyświetlenie obrazu na ekranie z zaznaczonymi wykrytymi twarzami i uśmiechami
 *
 * @autor Maciej Czernik
 * @version 1
 */
public class RealDetectionActivity extends Activity implements CvCameraViewListener2 {
    /**Kolor obramowania twarzy*/
    private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 0, 255, 255);
    /**Pojedyńczy uśmiech*/
    public  int        SingleSmile     = 0;
    /**Klatka wideo w kolorach*/
    private Mat                    mRgba;
    /**Klatka wideo w skali szarości*/
    private Mat                    mGray;
    /***/
    private CascadeClassifier mJavaDetector;
    /**Obiekt klasyfikatora kaskadowego, detekcja uśmiechu */
    private CascadeClassifier mSmileDetector;
    /**Plik zawierający klasyfikator detekcji twarzy*/
    private File mCascadeFile;
    /**Plik zawierający klasyfikator detekcji uśmiechu */
    private File smileCascadeFile;
    /**Stosunek wielkości twarzy do całego obrazu podczas detekcji twarzy*/
    private String                 setting_faceSize;
    /**Scalowanie podczas detekcji twarzy*/
    private Double                 setting_faceScale;
    /**Dokładność klasyfiaktora detekcji twarzy*/
    private int                    setting_faceNei;
    /**Stosunek wielkości twarzy do całego obrazu podczas detekcji uśmiechu*/
    private String                 setting_smileSize;
    /**Scalowanie podczas detekcji uśmiechu*/
    private Double                 setting_smileScale;
    /**Dokładność klasyfiaktora detekcji uśmiechu*/
    private int                    setting_smileNei;
    /**Relatywna wielkość twarzy*/
    private float mRelativeFaceSize = 0.3f;
    /**Relatywna wielkośc uśmiechu*/
    private float mRelativeSmileSize = 0.1f;
    /**Końcowa wielkość twarzy*/
    private int mAbsoluteFaceSize = 0;
    /**Końcowa wielkość uśmiechu*/
    private int mAbsoluteSmileSize = 0;
    /**Kamera*/
    private CameraBridgeViewBase   mOpenCvCameraView;

    /**
     * Metoda ładująca openCV i inicjalizująca klasyfikatory.
     *
     * @autor Maciej Czernik
     * @version 1
     */
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    try {
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        InputStream is1 = getResources().openRawResource(R.raw.haarcascade_smile);
                        File cascadeFaceDir = getDir("cascadeFace", Context.MODE_PRIVATE);
                        File cascadeSmileDir = getDir("cascadeSmile", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeFaceDir, "testface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);
                        smileCascadeFile = new File(cascadeSmileDir,"test.xml");
                        FileOutputStream os1 = new FileOutputStream(smileCascadeFile);
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();
                        byte[] buffer1 = new byte[4096];
                        int bytesRead1;
                        while ((bytesRead1 = is1.read(buffer1)) != -1) {
                            os1.write(buffer1, 0, bytesRead1);
                        }
                        is1.close();
                        os1.close();


                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        mSmileDetector = new CascadeClassifier(smileCascadeFile.getAbsolutePath());
                        if (mSmileDetector.empty()) {
                            mSmileDetector = null;
                            return;
                        } else
                        if (mJavaDetector.empty()) {
                            mJavaDetector = null;
                        } else
                        cascadeFaceDir.delete();
                        cascadeSmileDir.delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    /**
     * Konstruktor
     *
     * @autor Maciej Czernik
     * @version 1
     */
    public RealDetectionActivity() {
    }

    /**
     * Metoda wywołana przy starcie aktwności, ustawia pobrane parametry z Intentu.
     *
     * @autor Maciej Czernik
     * @version 1
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.face_detect_surface_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if(extras.getString("rule")!= null) SingleSmile =  1;
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
        }else {
        }
        setSettings();
    }

    /**
     * Metoda stopująca pobieranie obrazu z kamery.
     *
     * @autor Maciej Czernik
     * @version 1
     */
    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    /**
     * Metoda wznawiająca pobieranie orbazu z kamery.
     *
     * @autor Maciej Czernik
     * @version 1
     */
    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initOpenCV(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mLoaderCallback);
    }

    /**
     * Metoda wyłączająca obraz z kamery.
     *
     * @autor Maciej Czernik
     * @version 1
     */
    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    /**
     * Metoda wywołana przy starcie kamery, inicjalizuje macierze z obrazem.
     *
     * @param width -  Szerokość klatki wideo.
     * @param height - Wysokość klatki wideo.
     * @autor Maciej Czernik
     * @version 1
     */
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    /**
     * Metoda przy wywołana przy zatrzymaniu kamery. Zwalnia macierze z obrazem.
     *
     * @autor Maciej Czernik
     * @version 1
     */
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    /**
     * Metoda obsługująca każdą ramkę pobraną z kamery. Wykonuje detekcję twarzy i uśmiechu przy
     * pomocy klasyfikatorów.
     *
     * @param inputFrame Pobierana ramka.
     * @autor Maciej Czernik
     * @version 1
     * @return
     */
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
        }
        if (mAbsoluteSmileSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeSmileSize) > 0) {
                mAbsoluteSmileSize = Math.round(height * mRelativeSmileSize);
            }
        }

        MatOfRect faces = new MatOfRect();

            if (mJavaDetector != null)
                mJavaDetector.detectMultiScale(mGray, faces, setting_faceScale, setting_faceNei, 0,
                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());



        Rect[] facesArray = faces.toArray();
        Mat blackMat =new Mat (320, 240,  CvType.CV_8UC1);
        blackMat.setTo(new Scalar(1, 1, 1));
        for (int i = 0; i < facesArray.length; i++) {

            Rect face = facesArray[i];
            Double aspect_ratio = (double)face.width/face.height;
            Log.i("Face --------",aspect_ratio.toString());
            Point center= new Point();
            int radius;
            if( 0.75 < aspect_ratio && aspect_ratio < 1.3 )
            {
                center.x = (int)Math.round(face.x + face.width*0.5);
                center.y =  (int)Math.round(face.y + face.height*0.5);
                radius =  (int)Math.round((face.width + face.height)*0.25);
                Core.circle(mRgba, center, radius, FACE_RECT_COLOR, 3, 8, 0 );
            }
            MatOfRect smiles = new MatOfRect();
            Mat smallImgROI;
            Rect region_of_interest = new Rect(20,20,20,20);
            int half_height = Math.round((float) face.height*1/2);
            face.y=face.y + half_height;
            face.height =  (face.height*1/2)-1;
             Mat tmp = new Mat(mGray,face);

            mSmileDetector.detectMultiScale(tmp, smiles, setting_smileScale, setting_smileNei, 0
                    , new Size(mAbsoluteSmileSize, mAbsoluteSmileSize),
                    new Size());
            Rect[] smilesArray = smiles.toArray();
            if(smilesArray.length > 70 )   Core.rectangle(mRgba, face.tl(), face.br(), new Scalar(0, 255, 0, 2), 4);
            for (int j = 0; j < smilesArray.length; j++) {
                if (SingleSmile == 1) {
                    Intent result = new Intent();
                    result.putExtra("ret", getString(R.string.resoultTimer));
                    setResult(RESULT_OK, result);
                    finish();
                 }

            }

        }
        return mRgba;
    }

    /**
     * Metoda pobierająca parametry detekcji twarzy i uśmiechu z Intent.
     *
     * @autor Maciej Czernik
     * @version 1
     */
    public boolean setSettings() {
        if (setting_faceSize.equals("50"))
            setMinFaceSize(0.5f);
        else if (setting_faceSize.equals("40"))
            setMinFaceSize(0.4f);
        else if (setting_faceSize.equals("30"))
            setMinFaceSize(0.3f);
        else if (setting_faceSize.equals("20"))
            setMinFaceSize(0.2f);
        else if (setting_faceSize.equals("10"))
            setMinFaceSize(0.1f);
        else if (setting_faceSize.equals("5"))
            setMinSmileSize(0.05f);

        if (setting_smileSize.equals("50"))
            setMinSmileSize(0.5f);
        else if (setting_smileSize.equals("40"))
            setMinSmileSize(0.4f);
        else if (setting_smileSize.equals("30"))
            setMinSmileSize(0.3f);
        else if (setting_smileSize.equals("20"))
            setMinSmileSize(0.2f);
        else if (setting_smileSize.equals("10"))
            setMinSmileSize(0.1f);
        else if (setting_smileSize.equals("5"))
            setMinSmileSize(0.005f);
        return true;
    }

    /**
     * Metoda ustawia minimalną wilkosc twarzy.
     *
     * @autor Maciej Czernik
     * @version 1
     * @param faceSize
     */
    private void setMinFaceSize(float faceSize) {
        mRelativeFaceSize = faceSize;
        mAbsoluteFaceSize = 0;
    }

    /**
     * Metoda ustawia minimalną wilkość uśmiechu.
     *
     * @autor Maciej Czernik
     * @version 1
     * @param smileSize
     */
    private void setMinSmileSize(float smileSize) {

        mRelativeSmileSize = smileSize;
        mAbsoluteSmileSize = 0;
    }
}
