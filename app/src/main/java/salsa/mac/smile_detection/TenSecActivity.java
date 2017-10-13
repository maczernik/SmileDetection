package salsa.mac.smile_detection;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Klasa odpowiedzialna za modul minigry "Punkty za usmiech"
 *
 * @autor Maciej Czernik
 * @version 1
 */
public class TenSecActivity extends Activity implements CvCameraViewListener2 {
    /**Obiekty podglądu obrazu.*/
    private ImageView iv;
    /**Obiekt TextView z odliczanym czasem*/
    private TextView countView;
    /**Przycisk zarujący punkty*/
    private Button btn_tenMinRun;
    /**Kolor obramowania wykrytej twarzy*/
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    /**Macierz z kolorowym obrazem*/
    private Mat mRgba;
    /**Macierz z obrazem w skali szarości*/
    private Mat mGray;
    /**Obiekt klasyfikatora kaskadowego, detekcja twarzy*/
    private CascadeClassifier mJavaDetector;
    /**Obiekt klasyfikatora kaskadowego, detekcja uśmiechu */
    private CascadeClassifier mSmileDetector;
    /**Plik zawierający klasyfikator detekcji twarzy*/
    private File mCascadeFile;
    /**Plik zawierający klasyfikator detekcji uśmiechu */
    private File smileCascadeFile;
    /**Przełącznik trybu*/
    public int switchTime;
    /**Punkty*/
    public int points;
    /**Relatywna wielkość twarzy*/
    private float mRelativeFaceSize = 0.3f;
    /**Relatywna wielkośc uśmiechu*/
    private float mRelativeSmileSize = 0.1f;
    /**Końcowa wielkość twarzy*/
    private int mAbsoluteFaceSize = 0;
    /**Końcowa wielkość uśmiechu*/
    private int mAbsoluteSmileSize = 0;
    /** */
    private CameraBridgeViewBase   mOpenCvCameraView;

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
                    try {
                        // load cascade file from application resources
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        InputStream is1 = getResources().openRawResource(R.raw.haarcascade_smile);
                        File cascadeFaceDir = getDir("cascadeFace", Context.MODE_PRIVATE);
                        File cascadeSmileDir = getDir("cascadeSmile", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeFaceDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);
                        smileCascadeFile = new File(cascadeSmileDir,"haarcascade_smile.xml");
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
     * Metoda uruchomiona przy starcie aktywności. Inicjalizuje wartości początkowe.
     *
     * @param savedInstanceState
     * @autor Maciej Czernik
     * @version 1
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ten_min);
        setComponents();
        switchTime = 10;
        points = 0;
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tenMinVideo);
        mOpenCvCameraView.setCameraIndex(1);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    /**
     * Metoda ustawiająca komponenty.
     *
     * @autor Maciej Czernik
     * @version 1
     */
    private void setComponents() {
        iv = (ImageView) findViewById(R.id.tenMinimageView);
        countView = (TextView) findViewById(R.id.tenMinCountDown);
        countView.setText("Points: 0");
        iv.setImageDrawable(getResources().getDrawable(R.drawable.smile1));
        btn_tenMinRun = (Button) findViewById(R.id.btn_tenMinRun);
        btn_tenMinRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                points = 0;
            }
        });

    }

    /**
     * Metoda stopująca pobieranie obrazu z kamery.
     * @autor Maciej Czernik
     * @version 1
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    /**
     * Metoda wznawiająca pobieranie orbazu z kamery.
     * @autor Maciej Czernik
     * @version 1
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i("Face --------", "zzzz1");
       //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mLoaderCallback);
        OpenCVLoader.initOpenCV(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mLoaderCallback);
    }

    /**
     * Metoda wyłączająca obraz z kamery.
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

    /*
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
     * Metoda zmieniająca obraz ikony symbolizującej stan uśmiechu.
     *
     * @autor Maciej Czernik
     * @version 1
     */
    private void changeVariable() {
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              countView.setText("Points: " + new Integer(points).toString());
                              if (switchTime > 130) {
                                  if(points<9999999)
                                        points++;
                                  iv.setImageDrawable(getResources().getDrawable(R.drawable.smile1));
                              } else if (switchTime > 50) {
                                  iv.setImageDrawable(getResources().getDrawable(R.drawable.smile2));
                              } else {
                                  iv.setImageDrawable(getResources().getDrawable(R.drawable.smile3));
                              }
                          }
                      }
        );
    }

    /**
     * Metoda obsługująca każdą ramkę pobraną z kamery. Wykonuje detekcję twarzy i uśmiechu przy
     * pomocy klasyfikatorów.
     * @param inputFrame Ramka obrazu.
     * @autor Maciej Czernik
     * @version 1
     * @return
     */
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        changeVariable();
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
        Mat mRgbaT = mRgba.t();
        Mat mGrayT = mGray.t();
        Core.flip(mGray.t(), mGrayT, 0);
        if (mAbsoluteFaceSize == 0) {
            int height = mGrayT.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
        }
        MatOfRect faces = new MatOfRect();
        if (mJavaDetector != null) {
            mJavaDetector.detectMultiScale(mGrayT, faces, 1.1, 2, 0,
                    new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        }
        if (mAbsoluteSmileSize == 0) {
            int height = mGrayT.rows();
            if (Math.round(height * mRelativeSmileSize) > 0) {
                mAbsoluteSmileSize = Math.round(height * mRelativeSmileSize);
            }
        }
        Rect[] facesArray = faces.toArray();
        Mat blackMat =new Mat (320, 240,  CvType.CV_8UC1);
        blackMat.setTo(new Scalar(1, 1, 1));
        Core.rectangle(mRgbaT, new Point(0 + (2 * 15), 0), new Point(10 + (2 * 15), 0), FACE_RECT_COLOR, 3);
        for (int i = 0; i < facesArray.length; i++) {

            Rect face = facesArray[i];
            Double aspect_ratio = (double)face.width/face.height;
            Point center= new Point();
            int radius;
            if( 0.75 < aspect_ratio && aspect_ratio < 1.3 )
            {
                center.x = (int)Math.round(face.x + face.width*0.5);
                center.y =  (int)Math.round(face.y + face.height*0.5);
                radius =  (int)Math.round((face.width + face.height)*0.25);
                Core.circle(mGrayT, center, radius, FACE_RECT_COLOR, 3, 8, 0);
            }
            MatOfRect smiles = new MatOfRect();
            Mat smallImgROI;
            Rect region_of_interest = new Rect(20,20,20,20);
            int half_height = Math.round((float) face.height/2);
            face.y=face.y + half_height;
            face.height = half_height-1;
            Mat tmp = new Mat(mGrayT,face);
            mSmileDetector.detectMultiScale(tmp, smiles, 1.1, 0, 0
                    //      , new Size(0, 0), new Size(30,30));
                    , new Size(mAbsoluteSmileSize, mAbsoluteSmileSize), new Size());
            Rect[] smilesArray = smiles.toArray();
            if(smilesArray.length >70 ){
                if(switchTime < 200)
                switchTime += 5;

            }else{
                if(switchTime > 0)
                    switchTime -= 1;
            }
        }
        if(facesArray.length == 0 ){
            if(switchTime > 0)
                switchTime -= 2;

        }
        Imgproc.resize(mGrayT, mGrayT, mGray.size());
        return mGrayT;

    }

}
