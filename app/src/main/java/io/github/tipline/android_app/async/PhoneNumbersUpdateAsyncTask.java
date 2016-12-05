package io.github.tipline.android_app.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import io.github.tipline.android_app.R;


/**
 * Created by elimonent on 11/6/16.
 */

/*
takes airline ambassadors hidden page and abstracts phone numbers
 */
public class PhoneNumbersUpdateAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    public PhoneNumbersUpdateAsyncTask(Context context) {
        this.context = context;
    }
    @Override
    protected Boolean doInBackground(Void... params) {
        //get nums from internet
        String fullJsonStr = "";
        boolean noException = false;
        try {
            URL url = new URL("http://tipnumbers.airlineamb.org/mynumbers.txt");

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String str;

            while ((str = in.readLine()) != null) {
                // str is one line of text; readLine() strips the newline character(s)
                fullJsonStr += str + "\n";
            }
            in.close();
            noException = true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if (noException) {
            //save nums to storage
            String fileName = context.getString(R.string.phone_num_file);
            String tmpFileName = context.getString(R.string.phone_num_temp_file);
            FileOutputStream outputStream = null;
            try {
                outputStream = context.openFileOutput(tmpFileName, Context.MODE_PRIVATE);
                outputStream.write(fullJsonStr.getBytes());
                outputStream.close();

                renameAppFile(context, tmpFileName, fileName);
                System.out.println("wrote ");
                System.out.println(fullJsonStr);
                System.out.println("to " + fileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    //http://stackoverflow.com/questions/2446423/how-to-rename-an-private-file-of-my-application
    public void renameAppFile(Context context, String originalFileName, String newFileName) {
        File originalFile = context.getFileStreamPath(originalFileName);
        File newFile = new File(originalFile.getParent(), newFileName);
        if (newFile.exists()) {
            // Or you could throw here.
            context.deleteFile(newFileName);
        }
        originalFile.renameTo(newFile);
    }

}
