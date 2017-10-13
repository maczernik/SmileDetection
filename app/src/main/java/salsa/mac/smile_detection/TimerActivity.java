package salsa.mac.smile_detection;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Klasa odpowiedzialna za aktywność budzika. Wykorzystuje czasomierz, uruchamia alarm dzwiękowy.
 * Wywołuje RealDetectionActivity aby dokonać detekcji uśmiechu na obrazie z tylniej kamery
 * urządzenia mobilnego.
 *
 * @autor Maciej Czernik
 * @version 1
 */
public class TimerActivity extends AppCompatActivity {

    /**Wybór kontekstu dla RealDetectionActivity.*/
    static final int PICK_CONTACT_REQUEST = 1;
    /**Przycisk wyboru budzika.*/
    Button btn_timer_time;
    /**Przycisk wyboru czasomierza.*/
    Button btn_timer_nap;
    /**Przycisk uruchomienia odliczania.*/
    Button btn_timer_run;
    /**Obiekt TextView.*/
    TextView tv;
    /**Przełącznik budzika(1) na czasomierz(0). */
    int isRealTime = 1;
    /**Obiekt odtwarzacza muzyki.*/
    public MediaPlayer mp;

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
        mp = MediaPlayer.create(this, R.raw.audio_1);
        setContentView(R.layout.activity_timer);
        TimePicker tp = (TimePicker) findViewById(R.id.timePicker);
        tp.setIs24HourView(true);
        setActionToButtons();

    }

    /**
     * Metoda przypisująca akcje dla poszczególnych przcisków.
     *
     * @autor Maciej Czernik
     * @version 1
     */
    public void setActionToButtons() {
        Calendar calendar = Calendar.getInstance();
        btn_timer_time = (Button) findViewById(R.id.btn_timer_time);
        btn_timer_nap = (Button) findViewById(R.id.btn_timer_nap);
        btn_timer_run = (Button) findViewById(R.id.btn_timer_run);
        btn_timer_time.setBackgroundColor(0xAA221100);
        btn_timer_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_timer_time.setBackgroundColor(0xAA221100);
                btn_timer_nap.setBackgroundColor(0x55555500);
                btn_timer_run.setVisibility(View.VISIBLE);
                SimpleDateFormat sdf = new SimpleDateFormat("hh:ss");
                Date date = null;
                Calendar c = Calendar.getInstance();
                TimePicker tp = (TimePicker) findViewById(R.id.timePicker);
                tp.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
                tp.setCurrentMinute(c.get(Calendar.MINUTE));
                isRealTime = 1;
            }
        });
        btn_timer_nap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_timer_nap.setBackgroundColor(0xAA221100);
                btn_timer_time.setBackgroundColor(0x44444400);
                btn_timer_run.setVisibility(View.VISIBLE);
                isRealTime = 0;
                SimpleDateFormat sdf = new SimpleDateFormat("hh:ss");
                Date date = null;
                try {
                    date = sdf.parse("00:10");
                } catch (ParseException e) {
                }
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                TimePicker tp = (TimePicker) findViewById(R.id.timePicker);
                tp.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
                tp.setCurrentMinute(c.get(Calendar.MINUTE));
            }
        });
        btn_timer_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_timer_time.setVisibility(View.INVISIBLE);
                btn_timer_nap.setVisibility(View.INVISIBLE);
                btn_timer_run.setText(getString(R.string.close));
                TimePicker tp = (TimePicker) findViewById(R.id.timePicker);
                int timeCountDown = 0;
                if (isRealTime == 0) {
                    timeCountDown = tp.getCurrentHour() * 60 * 60 + tp.getCurrentMinute() * 60;
                } else {
                    Calendar c = Calendar.getInstance();
                    int realTime = c.get(Calendar.HOUR_OF_DAY) * 60 * 60 + c.get(Calendar.MINUTE) * 60;
                    int tmpTime = tp.getCurrentHour() * 60 * 60 + tp.getCurrentMinute() * 60;
                    timeCountDown = tmpTime - realTime;
                }
                final CountDownTimer timerOb = new CountDownTimer(timeCountDown * 1000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        TextView tv = (TextView) findViewById(R.id.timer_text);
                        long h, m, s, tmpS;
                        //3 na 60!
                        s = (millisUntilFinished / 1000) % 60;
                        m = (millisUntilFinished / (1000 * 60) % 60);
                        h = millisUntilFinished / (1000 * 60 * 60);
                        if (h < 10 && m < 10 && s < 10)
                            tv.setText("0" + h + ":0" + m + ":0" + s);
                        else if (h < 10 && m < 10 && s > 10)
                            tv.setText("0" + h + ":0" + m + ":" + s);
                        else if (h < 10 && m > 10 && s < 10)
                            tv.setText("0" + h + ":" + m + ":0" + s);
                        else if (h < 10 && m > 10 && s > 10)
                            tv.setText("0" + h + ":" + m + ":" + s);
                        else if (h < 10 && m > 10 && s < 10)
                            tv.setText("0" + h + ":" + m + ":" + s);
                        else if (h > 10 && m < 10 && s < 10)
                            tv.setText("" + h + ":0" + m + ":0" + s);
                        else if (h > 10 && m < 10 && s > 10)
                            tv.setText("" + h + ":0" + m + ":" + s);
                        else if (h > 10 && m > 10 && s < 10)
                            tv.setText("" + h + ":" + m + ":0" + s);
                        else if (h > 10 && m > 10 && s > 10)
                            tv.setText("" + h + ":" + m + ":" + s);
                    }

                    public void onFinish() {
                        btn_timer_time.setVisibility(View.VISIBLE);
                        btn_timer_nap.setVisibility(View.VISIBLE);
                        btn_timer_run.setText(getString(R.string.run));
                        TextView tv = (TextView) findViewById(R.id.timer_text);
                        Intent intent = new Intent(TimerActivity.this, RealDetectionActivity.class);
                        intent.putExtra("rule", "timer");
                        mp.start();

                        startActivityForResult(intent, PICK_CONTACT_REQUEST);
                    }
                }.start();
                btn_timer_run.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView tv = (TextView) findViewById(R.id.timer_text);
                        timerOb.cancel();
                        btn_timer_time.setVisibility(View.VISIBLE);
                        btn_timer_nap.setVisibility(View.VISIBLE);
                        btn_timer_run.setText(getString(R.string.run));
                        tv.setText("00:00:00");
                        setActionToButtons();
                    }
                });

            }
        });
    }

    /**
     * Metoda wywoływana przy powrocie z aktywności RealDetctionActivity. Przerywa alarm dziękowy. I ustawia
     * stan początkowy w aktywności.
     *
     * @param requestCode Rządana informacja
     * @param resultCode Otrzymana informacja z aktywności RealDetectionActivity
     * @param data Dane zwrócone z aktywności.
     * @autor Maciej Czernik
     * @version 1
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        tv = (TextView) findViewById(R.id.timer_text);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("ret");
                TextView tv = (TextView) findViewById(R.id.timer_text);
                mp.stop();
                mp.reset();
                btn_timer_run.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        btn_timer_time.setVisibility(View.INVISIBLE);
                        btn_timer_nap.setVisibility(View.INVISIBLE);
                        btn_timer_run.setText(getString(R.string.close));
                        TimePicker tp = (TimePicker) findViewById(R.id.timePicker);
                        int timeCountDown = 0;
                        if (isRealTime == 0) {
                            timeCountDown = tp.getCurrentHour() * 60 * 60 + tp.getCurrentMinute() * 60;
                        } else {
                            Calendar c = Calendar.getInstance();
                            int realTime = c.get(Calendar.HOUR_OF_DAY) * 60 * 60 + c.get(Calendar.MINUTE) * 60;
                            int tmpTime = tp.getCurrentHour() * 60 * 60 + tp.getCurrentMinute() * 60;
                            timeCountDown = tmpTime - realTime;
                        }
                        final CountDownTimer timerOb = new CountDownTimer(timeCountDown * 1000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                TextView tv = (TextView) findViewById(R.id.timer_text);
                                long h, m, s, tmpS;
                                //3 na 60!
                                s = (millisUntilFinished / 1000) % 60;
                                m = (millisUntilFinished / (1000 * 60) % 60);
                                h = millisUntilFinished / (1000 * 60 * 60);
                                if (h < 10 && m < 10 && s < 10)
                                    tv.setText("0" + h + ":0" + m + ":0" + s);
                                else if (h < 10 && m < 10 && s > 10)
                                    tv.setText("0" + h + ":0" + m + ":" + s);
                                else if (h < 10 && m > 10 && s < 10)
                                    tv.setText("0" + h + ":" + m + ":0" + s);
                                else if (h < 10 && m > 10 && s > 10)
                                    tv.setText("0" + h + ":" + m + ":" + s);
                                else if (h < 10 && m > 10 && s < 10)
                                    tv.setText("0" + h + ":" + m + ":" + s);
                                else if (h > 10 && m < 10 && s < 10)
                                    tv.setText("" + h + ":0" + m + ":0" + s);
                                else if (h > 10 && m < 10 && s > 10)
                                    tv.setText("" + h + ":0" + m + ":" + s);
                                else if (h > 10 && m > 10 && s < 10)
                                    tv.setText("" + h + ":" + m + ":0" + s);
                                else if (h > 10 && m > 10 && s > 10)
                                    tv.setText("" + h + ":" + m + ":" + s);
                            }

                            public void onFinish() {
                                btn_timer_time.setVisibility(View.VISIBLE);
                                btn_timer_nap.setVisibility(View.VISIBLE);
                                btn_timer_run.setText(getString(R.string.run));
                                TextView tv = (TextView) findViewById(R.id.timer_text);
                                Intent intent = new Intent(TimerActivity.this, RealDetectionActivity.class);
                                intent.putExtra("rule", "timer");
                                mp.start();

                                startActivityForResult(intent, PICK_CONTACT_REQUEST);
                            }
                        }.start();
                        btn_timer_run.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TextView tv = (TextView) findViewById(R.id.timer_text);
                                timerOb.cancel();
                                btn_timer_time.setVisibility(View.VISIBLE);
                                btn_timer_nap.setVisibility(View.VISIBLE);
                                btn_timer_run.setText(getString(R.string.run));
                                tv.setText("00:00:00");
                                setActionToButtons();
                            }
                        });

                    }
                });
                isRealTime = 0;
                tv.setText(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                TextView tv = (TextView) findViewById(R.id.timer_text);
                mp.stop();
                mp.reset();
                btn_timer_run.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn_timer_time.setVisibility(View.INVISIBLE);
                        btn_timer_nap.setVisibility(View.INVISIBLE);
                        btn_timer_run.setText(getString(R.string.close));
                        TimePicker tp = (TimePicker) findViewById(R.id.timePicker);
                        int timeCountDown = 0;
                        if (isRealTime == 0) {
                            timeCountDown = tp.getCurrentHour() * 60 * 60 + tp.getCurrentMinute() * 60;
                        } else {
                            Calendar c = Calendar.getInstance();
                            int realTime = c.get(Calendar.HOUR_OF_DAY) * 60 * 60 + c.get(Calendar.MINUTE) * 60;
                            int tmpTime = tp.getCurrentHour() * 60 * 60 + tp.getCurrentMinute() * 60;
                            timeCountDown = tmpTime - realTime;
                        }
                        final CountDownTimer timerOb = new CountDownTimer(timeCountDown * 1000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                TextView tv = (TextView) findViewById(R.id.timer_text);
                                long h, m, s, tmpS;
                                //3 na 60!
                                s = (millisUntilFinished / 1000) % 60;
                                m = (millisUntilFinished / (1000 * 60) % 60);
                                h = millisUntilFinished / (1000 * 60 * 60);
                                if (h < 10 && m < 10 && s < 10)
                                    tv.setText("0" + h + ":0" + m + ":0" + s);
                                else if (h < 10 && m < 10 && s > 10)
                                    tv.setText("0" + h + ":0" + m + ":" + s);
                                else if (h < 10 && m > 10 && s < 10)
                                    tv.setText("0" + h + ":" + m + ":0" + s);
                                else if (h < 10 && m > 10 && s > 10)
                                    tv.setText("0" + h + ":" + m + ":" + s);
                                else if (h < 10 && m > 10 && s < 10)
                                    tv.setText("0" + h + ":" + m + ":" + s);
                                else if (h > 10 && m < 10 && s < 10)
                                    tv.setText("" + h + ":0" + m + ":0" + s);
                                else if (h > 10 && m < 10 && s > 10)
                                    tv.setText("" + h + ":0" + m + ":" + s);
                                else if (h > 10 && m > 10 && s < 10)
                                    tv.setText("" + h + ":" + m + ":0" + s);
                                else if (h > 10 && m > 10 && s > 10)
                                    tv.setText("" + h + ":" + m + ":" + s);
                            }

                            public void onFinish() {
                                btn_timer_time.setVisibility(View.VISIBLE);
                                btn_timer_nap.setVisibility(View.VISIBLE);
                                btn_timer_run.setText(getString(R.string.run));
                                TextView tv = (TextView) findViewById(R.id.timer_text);
                                Intent intent = new Intent(TimerActivity.this, RealDetectionActivity.class);
                                intent.putExtra("rule", "timer");
                                mp.start();

                                startActivityForResult(intent, PICK_CONTACT_REQUEST);
                            }
                        }.start();
                        btn_timer_run.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TextView tv = (TextView) findViewById(R.id.timer_text);
                                timerOb.cancel();
                                btn_timer_time.setVisibility(View.VISIBLE);
                                btn_timer_nap.setVisibility(View.VISIBLE);
                                btn_timer_run.setText(getString(R.string.run));
                                tv.setText("00:00:00");
                                setActionToButtons();
                            }
                        });

                    }
                });
                isRealTime = 0;
                tv.setText("No smile");

            }
        }
    }

}
