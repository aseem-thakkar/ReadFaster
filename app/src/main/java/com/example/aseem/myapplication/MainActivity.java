package com.example.aseem.myapplication;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.InputStream;

import nl.siegmann.epublib.domain.Book;
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

            // Log the book's authors
            Log.d("epublib", "author(s): " + book.getMetadata().getAuthors());

            // Log the book's title
            Log.d("epublib", "title: " + book.getTitle());

            // Log the book's coverimage property
            Bitmap coverImage = BitmapFactory.decodeStream(book.getCoverImage()
                    .getInputStream());
            Log.d("epublib", "Coverimage is " + coverImage.getWidth() + " by "
                    + coverImage.getHeight() + " pixels");

            // Log the tale of contents
//            logTableOfContents(book.getTableOfContents().getTocReferences(), 0);
        } catch (Exception e) {
            Log.d("epublib", e.getMessage());
        }
    }
}
