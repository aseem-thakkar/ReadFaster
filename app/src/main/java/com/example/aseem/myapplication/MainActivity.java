package com.example.aseem.myapplication;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.jsoup.Jsoup;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AssetManager assetManager = getAssets();
        try {
            // find InputStream for book
            InputStream epubInputStream = assetManager
                    .open("books/sample.epub");

            // Load Book from inputStream
            Book book = (new EpubReader()).readEpub(epubInputStream);
            //Printing book info
//            // Log the book's authors
//            Log.i("epublib", "author(s): " + book.getMetadata().getAuthors());
//
//            // Log the book's title
//            Log.i("epublib", "title: " + book.getTitle());
//
//            // Log the book's coverimage property
////            Bitmap coverImage = BitmapFactory.decodeStream(book.getCoverImage()
////                    .getInputStream());
////            Log.i("epublib", "Coverimage is " + coverImage.getWidth() + " by "
////                    + coverImage.getHeight() + " pixels");
//
//            // Log the tale of contents
//            logTableOfContents(book.getTableOfContents().getTocReferences(), 0);
//            String a = book.getSpine().getResource(6).getTitle();
//            Log.d("coot", "" + a);

//            //METHOD 1
//            int size=book.getSpine().size();
//            Log.d("het","teh ssize"+size);
//            for (int i = 0; i < size; i++) {
//                InputStream is = book.getSpine().getSpineReferences().get(i).getResource().getInputStream();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//                StringBuilder sb = new StringBuilder();
//                String line = null;
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line + "\n");
//                }
//
//                String htmltext = sb.toString();
//                htmltext = htmltext.replaceAll("\\<.*?\\>", "");
//                Log.d("het", htmltext + "");
//            }

        } catch (Exception e) {
            Log.e("epublib", e.getMessage());
        }


        //Method 2:
        try {
            String entireContent = "";
            String textContent = "";

            InputStream epubInputStream = assetManager.open("books/sample.epub");

            // Load Book from inputStream
            Book book = (new EpubReader()).readEpub(epubInputStream);

            int fileNumber = book.getContents().size();


            for (int i = 0; i < fileNumber; i++) {
                InputStream inputStream = book.getContents().get(i).getInputStream(); // file .html
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

            try{
                FileInputStream axe = openFileInput("temp");
                Scanner b = new Scanner(new InputStreamReader(axe));
                String s;
                while((s=b.next())!=null){
                    Log.d("new",s);
                }
            }catch (Exception e){
                Log.d("new","didn't work out");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logTableOfContents(List<TOCReference> tocReferences, int depth) {
        if (tocReferences == null) {
            return;
        }
        for (TOCReference tocReference : tocReferences) {
            StringBuilder tocString = new StringBuilder();
            for (int i = 0; i < depth; i++) {
                tocString.append("\t");
            }
            tocString.append(tocReference.getTitle());
            Log.i("epublib", tocString.toString());

            logTableOfContents(tocReference.getChildren(), depth + 1);
        }
    }
}
