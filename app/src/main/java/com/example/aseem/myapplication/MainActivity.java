package com.example.aseem.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

public class MainActivity extends AppCompatActivity {
    TextView mid, first, last, s1, s2;
    TextView tri1;
    int freq, i;
    Runnable updater;
    long pointer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        i = 0;
        pointer=0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        AssetManager assetManager = getAssets();
        freq = 60000 / 30;

        long result = checkIfFileExists();
        if (result == 0) {
            Toast.makeText(this, "Caching the File coz I see it first time", Toast.LENGTH_SHORT).show();
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        startHandler(result);

    }

    private long checkIfFileExists() {
        try {
            FileInputStream axe = openFileInput("temp");
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            pointer = sharedPref.getLong("status", pointer);
            Toast.makeText(this, "Found old File! with status : "+ pointer, Toast.LENGTH_SHORT).show();
            return (pointer-7);
        } catch (Exception e) {
            return 0;
        }
    }

    private void startHandler(long p) {
        mid = findViewById(R.id.word);
        first = findViewById(R.id.word1);
        last = findViewById(R.id.word2);
        s1 = findViewById(R.id.sentence1);
        s2 = findViewById(R.id.sentence2);
        tri1=findViewById(R.id.tri1);
        s1.setText("");
        s2.setText("");

        Scanner b = null;
        try {
            FileInputStream axe = openFileInput("temp");
            b = new Scanner(new InputStreamReader(axe));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(p!=-1){
            for(long i = 0;i<p;i++)
                b.next();
            pointer=p;
        }

        s1.setText(s1.getText()+b.next()+" "+b.next());


        String tempInit = b.next();
        int l = tempInit.length();
        first.setText(tempInit.substring(0, l/ 2));
        mid.setText(tempInit.substring(l / 2, l / 2 + 1));
        last.setText(tempInit.substring(l / 2 + 1));

        s2.setText(s2.getText()+" "+ b.next()+" "+b.next()+" "+b.next()+" "+b.next());

        final Handler timerHandler = new Handler();
        final Scanner finalB = b;

        pointer+=7;

        updater = new Runnable() {
            @Override
            public void run() {

//                Log.d("seb",".\ns1 : "+s1.getText()+"\nword :"+first.getText()+mid.getText()+last.getText()+"\ns2 :"+ s2.getText()+"\n=================================\n");
                s1.setText(s1.getText() +" "+first.getText()+mid.getText()+last.getText());
                while(s1.getText().length()>30){
                    int x=s1.getText().toString().indexOf(" ");
                    s1.setText(s1.getText().toString().substring(x+1));
//                    pointer++;
                }

                String a = s2.getText().toString().trim();
                int indexOfSpace = a.indexOf(" ");
                a=a.substring(0,indexOfSpace);
                if(s2.getText().length()<55)
                {
                    s2.setText((s2.getText().toString().substring(indexOfSpace+1))+" "+finalB.next());
                    pointer++;
                }
                else
                    s2.setText(s2.getText().toString().substring(indexOfSpace+1));

                int size = a.length();
                String b = a.substring(0, size / 2);
                String c = a.substring(size / 2, size / 2 + 1);
                String d = a.substring(size / 2 + 1);
                first.setText(b);
                mid.setText(c);
                last.setText(d);
//                tri1.setText(pointer+"");
                timerHandler.postDelayed(updater, freq);
            }
        };
        timerHandler.post(updater);

    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor status = sharedPref.edit();
        status.putLong("status", pointer);
        status.commit();
        Toast.makeText(this, "saved the status as "+pointer, Toast.LENGTH_SHORT).show();
    }
}
