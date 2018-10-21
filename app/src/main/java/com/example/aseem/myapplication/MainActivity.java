package com.example.aseem.myapplication;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;

import org.jsoup.Jsoup;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

public class MainActivity extends AppCompatActivity {
    TextView mid,first,last;
    int freq, i;
    Runnable updater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        i = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        AssetManager assetManager = getAssets();

        freq = 60000 / 140;

        try {

            String entireContent = "";
            String textContent = "";

            InputStream epubInputStream = assetManager.open("books/sample.epub");

            // Load Book from inputStream
            Book book = (new EpubReader()).readEpub(epubInputStream);

            int fileNumber = book.getContents().size();


            for (int i = 1; i < fileNumber; i++) {
                InputStream inputStream = book.getContents().get(i).getInputStream();
                try {
                    Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
                    entireContent = scanner.hasNext() ? scanner.next() : "";
                } finally {
                    inputStream.close();
                }

                org.jsoup.nodes.Document doc = Jsoup.parse(entireContent);
                textContent += doc.body().text();
                textContent += "\n\n";
            }

            FileOutputStream outputStream = openFileOutput("temp", Context.MODE_PRIVATE);
            outputStream.write(textContent.getBytes());
            outputStream.close();

            startHandler();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startHandler() {
        mid = findViewById(R.id.word);
        first = findViewById(R.id.word1);
        last = findViewById(R.id.word2);
        Scanner b = null;
        try {
            FileInputStream axe = openFileInput("temp");
            b = new Scanner(new InputStreamReader(axe));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (b != null) {
            final Handler timerHandler = new Handler();
            final Scanner finalB = b;
            updater = new Runnable() {
                @Override
                public void run() {
                    String a =finalB.next();
                    int size=a.length();
                    String b = a.substring(0,size/2);
                    String c = a.substring(size/2,size/2+1);
                    String d = a.substring(size/2+1);
                    first.setText(b);
                    mid.setText(c);
                    last.setText(d);
                    timerHandler.postDelayed(updater, freq);
                }
            };
            timerHandler.post(updater);
        }

    }

}
