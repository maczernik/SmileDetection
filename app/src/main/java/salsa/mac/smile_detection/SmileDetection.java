package salsa.mac.smile_detection;

import android.content.Context;
import android.util.Log;
import java.util.UUID;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Klasa odpowiedzialna za detekcje usmiechu na pojedynczym zdjeciu.
 *
 * @autor Maciej Czernik
 * @version 1
 */
public class SmileDetection {
    /**Obiekt klasyfikatora kaskadowego, detekcja twarzy*/
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

    /**
     * Konstruktor inicjuje klasyfikatory z podanych plików.
     *
     * @param isFace Strumień z klasyfikatorem detekcji twarzy.
     * @param isSmile Strumień z klasyfikatorem detekcji uśmiechu.
     * @param cascadeFaceDir Plik z klasyfikatorem detekci twarzy.
     * @param cascadeSmileDir Plik z klasyfikatorem detekcji uśmiechu.
     * @autor Maciej Czernik
     * @version 1
     */
    SmileDetection(InputStream isFace,InputStream isSmile,File cascadeFaceDir,File cascadeSmileDir){
        try{
            mCascadeFile = new File(cascadeFaceDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);
            smileCascadeFile = new File(cascadeSmileDir,"haarcascade_smile.xml");
            FileOutputStream os1 = new FileOutputStream(smileCascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = isFace.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            isFace.close();
            os.close();
            byte[] buffer1 = new byte[4096];
            int bytesRead1;
            while ((bytesRead1 = isSmile.read(buffer1)) != -1) {
                os1.write(buffer1, 0, bytesRead1);
            }
            isSmile.close();
            os1.close();
         } catch (IOException e) {
                e.printStackTrace();
         }
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
    }

    /**
     * Wykrywa uśmiechy na zdjęciu i zapsuje je w pamięci urządzenia mobilnego z losowym umerem UUID.
     *
     * @param path ścieżka do folderu z plikiem 1.jpg na którym przeprowadzona zostanie detekcja.
     * @autor Maciej Czernik
     * @version 1
     */
    public void saveSmilesOnPhoto(String path){
        Mat m = Highgui.imread(path+"1.jpg");
        Mat gray = new Mat();
        Imgproc.cvtColor(m, gray, Imgproc.COLOR_RGB2GRAY);
        int height = gray.rows();
        if (Math.round(height * mRelativeFaceSize) > 0) {
            mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
        }

        if (Math.round(height * mRelativeSmileSize) > 0) {
            mAbsoluteSmileSize = Math.round(height * mRelativeSmileSize);
        }

        MatOfRect faces = new MatOfRect();
        if (mJavaDetector != null)
            mJavaDetector.detectMultiScale(gray, faces, setting_faceScale, 2, 2,
                    new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {
            Rect face = facesArray[i];
            MatOfRect smiles = new MatOfRect();
            Mat tmp = new Mat(gray,face);
            mSmileDetector.detectMultiScale(tmp, smiles, setting_smileScale,setting_smileNei, 0
                    ,new Size(mAbsoluteSmileSize, mAbsoluteSmileSize), new Size());
            Rect[] smilesArray = smiles.toArray();
            if(smilesArray.length>60){
                UUID uuid = UUID.randomUUID();
                Mat submat = m.submat(facesArray[i]);
                Highgui.imwrite(path + uuid.toString()+ ".jpg", submat);
            }
        }
    }

    /**
     * Metoda odnajduje uśmiechy na obrazie 1.jpg, zaznacza je na obrazie i zapisuje w pamięci urządzenia
     * mobilnego pod nazwą 2.jpg.
     *
     * @param path ścieżka do folderu z plikiem 1.jpg na którym doknywana jest detekcja uśmiechu
     * @autor Maciej Czernik
     * @version 1
     */
    public void setSmilesOnPhoto(String path){
        Mat m = Highgui.imread(path+"1.jpg");
        Mat gray = new Mat();
        Imgproc.cvtColor(m, gray, Imgproc.COLOR_RGB2GRAY);
        int height = gray.rows();
        if (Math.round(height * mRelativeFaceSize) > 0) {
            mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
        }

        if (Math.round(height * mRelativeSmileSize) > 0) {
            mAbsoluteSmileSize = Math.round(height * mRelativeSmileSize);
        }

        MatOfRect faces = new MatOfRect();
        if (mJavaDetector != null)
        mJavaDetector.detectMultiScale(gray, faces, setting_faceScale, setting_faceNei, 2,
                new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {Rect face = facesArray[i];
            Double aspect_ratio = (double)face.width/face.height;
            Point center= new Point();
            int radius;
            if( 0.75 < aspect_ratio && aspect_ratio < 1.3 )
            {
                center.x = (int)Math.round(face.x + face.width*0.5);
                center.y =  (int)Math.round(face.y + face.height*0.5);
                radius =  (int)Math.round((face.width + face.height)*0.25);
                Core.circle(m, center, radius, new Scalar(255, 0, 0, 3), 3, 8, 0 );
            }
            MatOfRect smiles = new MatOfRect();
            Mat tmp = new Mat(gray,face);
            mSmileDetector.detectMultiScale(tmp, smiles, setting_smileScale,setting_smileNei, 0
                    ,new Size(mAbsoluteSmileSize, mAbsoluteSmileSize), new Size());
            Rect[] smilesArray = smiles.toArray();
            if(smilesArray.length>60){
                Core.rectangle(m, face.tl(), face.br(), new Scalar(0, 255, 0, 3), 4);
            }
        }
        Highgui.imwrite(path+"2.jpg",m);
    }

    /**
     * Metoda ustawia parametry detekcji twarzy i uśmiechu.
     *
     * @param fSize Wielkość twarzy.
     * @param fScale Skalsa twarzy.
     * @param fNei Dokładność algorytmu detekcji twarzy.
     * @param sSize Wielkość uśmiechu.
     * @param sScale Skala usmiechu.
     * @param sNei Dokładność algorytmu detekcji uśmiechu.
     * @autor Maciej Czernik
     * @version 1
     */
    public void setSettings(String fSize, Double fScale, int fNei,String sSize, Double sScale, int sNei){
        setting_faceSize= fSize;
        setting_faceScale=fScale;
        setting_faceNei=fNei;
        setting_smileSize=sSize;
        setting_smileScale=sScale;
        setting_smileNei=sNei;
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
    }

    /**
     * Metoda ustawia minimalną wielkośc twarzy na obrazie.
     *
     * @param faceSize Porządana wielkość twarzy.
     * @autor Maciej Czernik
     * @version 1
     */
    private void setMinFaceSize(float faceSize) {
        mRelativeFaceSize = faceSize;
        mAbsoluteFaceSize = 0;
    }

    /**
     * Metoda ustawia minimalną wielkość uśmiechu na obrazie.
     *
     * @param smileSize Porządana wielkość usmiechu.
     * @autor Maciej Czernik
     * @version 1
     */
    private void setMinSmileSize(float smileSize) {

        mRelativeSmileSize = smileSize;
        mAbsoluteSmileSize = 0;
    }

}
