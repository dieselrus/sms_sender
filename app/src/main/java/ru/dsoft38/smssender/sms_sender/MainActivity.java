package ru.dsoft38.smssender.sms_sender;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MainActivity extends ActionBarActivity {

    ImageButton btnBrowse;
    ImageButton btnStart;
    ImageButton btnPause;
    ImageButton btnStop;
    ImageButton btnClear;

    static TextView tvPhoneNumberListFilePatch;
    static TextView tvPhoneNumberCount;
    TextView tvPhoneNumberPathFile;
    TextView tvMessage;

    EditText editMessageTest;

    ProgressBar progressBar;

    int CurrentIndexSMS = 0;
    int maxSMSLen = 160;
    int smsCount = 1;
    boolean isStop = false;

    static List<String> strNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBrowse   = (ImageButton) findViewById(R.id.imgButtonBrowse);
        btnStart    = (ImageButton) findViewById(R.id.imgButtonSend);
        btnPause    = (ImageButton) findViewById(R.id.imgButtonPause);
        btnStop     = (ImageButton) findViewById(R.id.imgButtonStop);

        editMessageTest = (EditText) findViewById(R.id.editMessageText);

        tvPhoneNumberListFilePatch  = (TextView) findViewById(R.id.tvPhoneNumberListPath);
        tvPhoneNumberPathFile       = (TextView) findViewById(R.id.tvPhoneNumberPathFile);
        tvPhoneNumberCount          = (TextView) findViewById(R.id.tvPhoneNumCount);
        tvMessage                   = (TextView) findViewById(R.id.tvMessage);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Назначаем обработчик нажатия на кнопку Отправить СМС
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Делаем кнопку не активной
                btnBrowse.setEnabled(false);
                btnStart.setEnabled(false);

                // Выбран файл с номерами телефонов
                if(tvPhoneNumberListFilePatch.getText().length() == 0 || strNumbers.size() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Выберите файл со списком номеров для отправки!",
                            Toast.LENGTH_LONG).show();
                    return;
                } else {
                    //List<String> strNumbers = readFile(tvPhoneNumberListFilePatch.getText().toString());
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
                        if(num.length() == 11 || (num.substring(0, 1).equals("+") && num.length() == 12)) {
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

                // Чтение списка номеров из файла
                //strNumbers = readFile(tvPhoneNumberListFilePatch.getText().toString());

                //tvPhoneNumberListFilePatch.setText(fd.getContext().);
                //tvPhoneNumberPathFile.setText(getResources().getString(R.string.phoneNumberList) + " (" + String.valueOf(strNumbers.size()) + ")");

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

        //Обработка ввода символов в текстовое поле для текста СМС
        editMessageTest.addTextChangedListener(new TextWatcher()  {
            @Override
            public void afterTextChanged(Editable s) {
                //imgStatus.setVisibility(View.INVISIBLE);
                //tvMessageText.setText(getResources().getString(R.string.MessageText) +
                // " (" + String.valueOf(MAX_LENGTH_SMS - strMyName.length() - txtSMSText.length()) + ")");

                //String strCurrentSMS = "1";

                if(isCyrillic(editMessageTest.getText().toString())){
                    maxSMSLen = 70;
                } else {
                    maxSMSLen = 160;
                }

                smsCount = (int)(editMessageTest.getText().length() / maxSMSLen) + 1;
                String strCurrentSMS = String.valueOf(smsCount);

                String totalSMSLen = String.valueOf(editMessageTest.getText().length()) ;

                tvMessage.setText(getResources().getString(R.string.messageText) + " (" + totalSMSLen + "/" + strCurrentSMS + ")");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

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

        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        registerReceiver(new SendSms(), new IntentFilter(SENT));
        registerReceiver(new DeliverySms(), new IntentFilter(DELIVERED));

        try {
            SmsManager smsManager = SmsManager.getDefault();
            //if(smsCount > 1){
                ArrayList mArray = smsManager.divideMessage(message);
                ArrayList sentArrayIntents = new ArrayList();
                ArrayList deliveredArrayIntents = new ArrayList();

                for(int i = 0; i < mArray.size(); i++) {
                    sentArrayIntents.add(sentPI);
                    deliveredArrayIntents.add(deliveredPI);
                }

                smsManager.sendMultipartTextMessage(phoneNo, null, mArray, sentArrayIntents, deliveredArrayIntents);
            //} else {
            //    smsManager.sendTextMessage(phoneNo, null, message, null, null);
            //}
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
    static List<String> readFile(String filePath){

        List<String> strNumbers = new Vector<String>();
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
            Log.d("Data", e.getMessage().toString());
        }

        //Log.d("Data", text);

        return strNumbers;
    }

    // Определение языка (Кирилица или нет)
    boolean isCyrillic(String _str){
        for(int i = 0; i < _str.length(); i++){
            //String hexCode = Integer.toHexString(_str.codePointAt(i)).toUpperCase();
            int hexCode = _str.codePointAt(i);
            //Log.d("Data", String.valueOf(hexCode));

            if(hexCode > 1040 && hexCode < 1103){
                return true;
            }

        }
        return false;
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
        // Чтение списка номеров из файла
        strNumbers = readFile(tvPhoneNumberListFilePatch.getText().toString());

        tvPhoneNumberCount.setText("(" + String.valueOf(strNumbers.size()) + ")");
        tvPhoneNumberListFilePatch.setText(path);
    }
}