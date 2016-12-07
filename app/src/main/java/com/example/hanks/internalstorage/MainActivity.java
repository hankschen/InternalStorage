package com.example.hanks.internalstorage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Spinner spFile;
    TextView tvShow, tvPath;
    EditText etInput, etFileName;
    Button btnSave, btnAppend, btnLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findviews();
    }

    //開啟此app時,spinner下拉選單就顯示出所有檔名
    @Override
    protected void onStart() {
        super.onStart();
        setupSpinner();
    }

    void findviews() {
        spFile = (Spinner) findViewById(R.id.spinner);
        tvShow = (TextView) findViewById(R.id.textView);
        tvPath = (TextView) findViewById(R.id.textView2);
        etInput = (EditText) findViewById(R.id.editText);
        btnSave = (Button) findViewById(R.id.button);
        btnAppend = (Button) findViewById(R.id.button2);
        btnLoad = (Button) findViewById(R.id.button3);
    }


    public void onSave(View v) {
        View myView = getLayoutInflater().inflate(R.layout.dialog_view, null);
        etFileName = (EditText) myView.findViewById(R.id.editText2);
        new AlertDialog.Builder(this)
                .setTitle(R.string.SaveFile)
                .setView(myView)
                .setCancelable(false)
                .setPositiveButton(R.string.Ok, dlgBtnClick)
                .setNegativeButton(R.string.Cancel, dlgBtnClick)
                .show();
    }

    //建立對話視窗
    DialogInterface.OnClickListener dlgBtnClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    String fileName = etFileName.getText().toString();
                    //判斷使用者有沒有輸入檔案名稱
                    if (fileName.equals("") || fileName == null) {
                        Toast.makeText(MainActivity.this, "No FileName...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);//MODE_PRIVATE開新檔案或覆蓋檔案
                        fos.write((etInput.getText().toString() + "\n").getBytes());
                        fos.close();
                        Toast.makeText(MainActivity.this, "File is saved!", Toast.LENGTH_SHORT).show();
                        tvPath.setText(getFilesDir().toString());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                    }
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    Toast.makeText(MainActivity.this, R.string.CancelFile, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void onAppend(View v) {
        try {
            FileOutputStream fos = openFileOutput(spFile.getSelectedItem().toString(), MODE_APPEND);//MODE_APPEND附加資料到資料後面
            fos.write((etInput.getText().toString() + "\n").getBytes());
            fos.close();
            Toast.makeText(MainActivity.this, "Data is appended!", Toast.LENGTH_SHORT).show();
            onLoad(btnAppend);//此地訪不用到view,但此方法需有參數,所以隨便給他一個view,他也不會用到
            //或是在建一個onLoad(){}沒參數的方法,即為方法多載
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
        }
    }

    public void onLoad(View v) {
        try {
            //我們是要設計成從spinner選擇檔案,將檔名顯示在spinner,不是要觸發事件,所以不是用Listener監聽
            //而是要按下"讀取"才會觸發事件顯示內容
            FileInputStream fis = openFileInput(spFile.getSelectedItem().toString());
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            tvShow.setText(new String(buffer));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
        }
    }

    void setupSpinner() {
        String[] fileNames = fileList();
        //判斷沒有檔案名稱時跳回
        if (fileNames == null) {
            return;
        }
        ArrayAdapter<String> adt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, fileNames);
        //下拉選單用內定樣式
        adt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFile.setAdapter(adt);
    }
}
