package com.mierzejewski.inz.model;

/**
 * Created by dom on 28/03/15.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;


public class DataBaseExporter
{
    private final WeakReference<Context> context;
    private final String dataBaseName;

    public DataBaseExporter(Context context, String dataBaseName)
    {
        this.context = new WeakReference<Context>(context);
        this.dataBaseName = dataBaseName;
    }

    private void copyFile(File source, File target)
            throws IOException
    {
        FileInputStream fileInputStream = new FileInputStream(source);
        FileOutputStream fileOutputStream = new FileOutputStream(target);

        try
        {
            FileChannel inChannel = fileInputStream.getChannel();
            FileChannel outChannel = fileOutputStream.getChannel();

            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
        finally
        {
            if (fileInputStream != null)
                fileInputStream.close();
            if (fileOutputStream != null)
                fileOutputStream.close();
        }
    }

    private boolean isExternalStorageWritable()
    {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            return true;
        }

        return false;
    }

    private File getPublicDirectory()
    {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        path.mkdirs();

        return path;
    }

    private File getTargetDatabaseFile()
    {
        return new File(getPublicDirectory(), dataBaseName);
    }

    public String backup()
    {
        final StringBuilder sbLog = new StringBuilder();

        final File sourceFile = context.get().getDatabasePath(dataBaseName);

        sbLog.append("EKSPORT bazy danych:");
        sbLog.append("\n");
        sbLog.append(sourceFile.toString());
        sbLog.append("\n");

        if (isExternalStorageWritable())
        {
            final File targetPath = getPublicDirectory();

            sbLog.append("do katalogu:");
            sbLog.append("\n");
            sbLog.append(targetPath.toString());
            sbLog.append("\n");

            if (targetPath.isDirectory())
            {
                final File targetFile = getTargetDatabaseFile();

                try
                {
                    if (targetFile.exists())
                    {
                        targetFile.delete();
                    }

                    copyFile(sourceFile, targetFile);

                    targetFile.setReadable(true, false);

                    context.get().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(targetFile)));

                    if (targetFile.exists())
                    {
                        sbLog.append("kopiowanie pliku zakończone");
                        sbLog.append("\n");
                    }
                    else
                    {
                        sbLog.append("kopiowanie pliku bazy NIEUDANE (plik nie został znaleziony)");
                        sbLog.append("\n");
                    }
                }
                catch (IOException e)
                {
                    sbLog.append("kopiowanie pliku bazy NIEUDANE:");
                    sbLog.append("\n");
                    sbLog.append(e.toString());
                    sbLog.append("\n");
                }
            }
            else
            {
                sbLog.append("katalog docelowy NIEDOSTĘPNY");
                sbLog.append("\n");
            }
        }
        else
        {
            sbLog.append("Pamięć external storage NIEDOSTĘPNA");
            sbLog.append("\n");
        }

        return sbLog.toString();
    }

}
