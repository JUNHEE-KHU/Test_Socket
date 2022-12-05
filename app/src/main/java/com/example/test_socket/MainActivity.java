package com.example.test_socket;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    String domain = "khu.ac.kr";   //>> 서버 주소
    String path = "kor/upload/catalog/catalog_file_1668646914127.jpg";   //>> 서버 내 이미지파일 경로
    Socket sock = null;
    CSoket th;    //>> Thread 클래스


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        th = new CSoket();
    }

    // 이미지를 불러오기 위한 함수
    public void onClickForLoad(View v)
    {
        // Thread 동작
        th.start();

        Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
    }

    // 안드로이드에서 네트워크 작업을 하기 위해서는 Thread나 AsyncTask가 필요하다
    // 그래서 CLoadImage를 만들어 Thread를 extend
    private class CSoket extends Thread {

        public CSoket() {
        }

        public void run() {
            try {
                // klas.khu.ac.kr:80 로 접속
                sock = new Socket(domain, 80);

                // 서버로 GET 요청
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())));
                out.println("GET " + path + " HTTP/1.1\n" +
                        "Host: MobileProgramming\n" +
                        "User-Agent: Mozilla/4.0 (compatible: MSIE 7.0; Windows NT 5.1)\n\n");
                out.flush();

                // 저장할 파일 열기
                String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();    //>> 최상위 절대 경로
                FileOutputStream output = new FileOutputStream(ex_storage + "/DCIM/image.jpg");

                // 서버에서 요청한 이미지 파일 받기
                InputStream is = sock.getInputStream();

                int readBytes; //>> 버퍼의 길이

                // 서버에게 요청한 데이터 받기
                // 헤더
                ByteArrayOutputStream byteArrayOutputStreamTemp = new ByteArrayOutputStream(278);
                byte[] bufTemp = new byte[278];
                readBytes = is.read(bufTemp);

                // 서버에게 요청한 데이터 받기
                // 이미지
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                byte[] buf = new byte[1024];
                while ((readBytes = is.read(buf)) != -1) { //보낸것을 딱맞게 받아 write 합니다.
                    output.write(buf, 0, readBytes);
                }

                output.close();
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}