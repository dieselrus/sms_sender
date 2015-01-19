package ru.dsoft38.smssender.sms_sender;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    ImageButton btnBrowse;
    ImageButton btnStart;
    ImageButton btnPause;
    ImageButton btnStop;
    ImageButton btnClear;

    static TextView tvPhoneNumberListFilePatch;
    EditText editMessageTest;

    ProgressBar progressBar;

    int CurrentIndexSMS = 0;
    boolean isStop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBrowse = (ImageButton) findViewById(R.id.imgButtonBrowse);
        btnStart = (ImageButton) findViewById(R.id.imgButtonSend);
        btnPause = (ImageButton) findViewById(R.id.imgButtonPause);
        btnStop = (ImageButton) findViewById(R.id.imgButtonStop);

        editMessageTest = (EditText) findViewById(R.id.editMessageText);
        tvPhoneNumberListFilePatch = (TextView) findViewById(R.id.tvPhoneNumberListPath);

        // Назначаем обработчик нажатия на кнопку Отправить СМС
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Делаем кнопку не активной
                btnBrowse.setEnabled(false);
                btnStart.setEnabled(false);

                // Выбран файл с номерами телефонов
                if(tvPhoneNumberListFilePatch.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Выберите файл со списком номеров для отправки!",
                            Toast.LENGTH_LONG).show();
                    return;
                } else {
                    List<String> strNumbers = readFile(tvPhoneNumberListFilePatch.getText().toString());
                    String message = editMessageTest.getText().toString();

                    int maxCount = strNumbers.size();

                    if (maxCount > 100)
                        maxCount = 100;

                    progressBar.setMax(maxCount);

                    for (int i = CurrentIndexSMS; i < maxCount; i++) {
                        // Если нажали остановку или паузу отправки
                        if(isStop)
                        {
                            Toast.makeText(getApplicationContext(),
                                    "Отправка остановлена",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Удаляем не нужные символы
                        String num = strNumbers.get(i).replace("-", "").replace(";", "").replace(" ", "").trim() ;

                        // Проверяем длину номера 11 символов или 12, если с +
                        if(num.length() == 11 || (num.substring(1, 1) == "+" && num.length() == 12)) {
                            sendSMSMessage(num, message);
                        }

                        CurrentIndexSMS = i + 1;
                        progressBar.setProgress(CurrentIndexSMS);
                    }
                }
            }
        });

        // Назначаем обработчик нажатия на кнопку выбора файла
        btnBrowse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                OpenFileDialog fd = new OpenFileDialog(MainActivity.this ).setFilter(".*\\.txt");
                fd.show();
                //tvPhoneNumberListFilePatch.setText(fd.getContext().);

                btnStart.setEnabled(true);
                btnPause.setEnabled(true);
                btnStop.setEnabled(true);
            }
        });

        // Назначаем обработчик нажатия на кнопку приостановки отправки
        btnPause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Останавливаем отправку
                isStop = true;

                btnStart.setEnabled(true);
                btnPause.setEnabled(false);
                btnStop.setEnabled(true);
            }
        });

        // Назначаем обработчик нажатия на кнопку остановки отправки
        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Останавливаем отправку
                isStop = true;
                CurrentIndexSMS = 0;

                btnStart.setEnabled(true);
                btnPause.setEnabled(true);
                btnStop.setEnabled(false);

                btnBrowse.setEnabled(true);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Отправка СМС
    protected void sendSMSMessage(String phoneNo, String message) {
        Log.i("Send SMS", "Phone: " + phoneNo);

        //String phoneNo = txtphoneNo.getText().toString();
        //String message = txtMessage.getText().toString();

        try {
            //SmsManager smsManager = SmsManager.getDefault();
            //smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS faild, please try again.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    // Чтение файла с номерами
    List<String> readFile(String filePath){

        List<String> strNumbers = null;
        //File sdcard = Environment.getExternalStorageDirectory();

        //Создаём объект файла
        //File file = new File(sdcard, filePath);
        File file = new File(filePath);

        //Read text from file
        //StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                //text.append(line);
                //text.append('\n');
                strNumbers.add(line.trim());
            }
        }
        catch (IOException e) {
            //Ошибка
        }

        //Log.d("Data", text);

        return strNumbers;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    static public void setFilePath(String path){
        tvPhoneNumberListFilePatch.setText(path);
    }
}