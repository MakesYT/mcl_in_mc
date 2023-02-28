package top.ncserver.mclmc;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static top.ncserver.mclmc.MclMC.charset;

public class Tools {
    public static void downloadFile(String url, String path){

        try {
            // Create a URL object
            URL u = new URL(url);

            // Open a connection
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();

            // Set some properties
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);

            // Connect to the server
            conn.connect();

            // Get an input stream from the connection
            InputStream is = conn.getInputStream();

            // Create a file output stream
            FileOutputStream fos = new FileOutputStream(path);

            // Define a buffer size
            int bufferSize = 1024;

            // Define a byte array to store data
            byte[] buffer = new byte[bufferSize];

            // Define a variable to store the number of bytes read
            int len;

            // Loop through the input stream and write data to the output stream
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                fos.flush();
            }
            fos.close();
            is.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void unzipMclJar(String zip,String path) {
        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zip));

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                if (name.equals("mcl.jar")){
                    File file;
                    if (charset.equals("GBK")){
                         file = new File(path+"\\"+name);
                    }else {
                        file = new File(path+"/"+name);
                    }

                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zis.read(buffer)) != -1) {
                        bos.write(buffer, 0, len);
                    }

                    bos.close();
                }
            }
            zis.close();
            File zip1 = new File(zip);
            zip1.delete();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
