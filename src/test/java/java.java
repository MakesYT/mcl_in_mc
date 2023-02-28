import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import top.ncserver.mclmc.Tools;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class java {
    public static void main(String[] args) {
        try {

            // processBuilder.();

            try {
                Process process = Runtime.getRuntime().exec("cmd.exe /k");
                new Thread(() -> {
                    BufferedReader readerErr = new BufferedReader(new InputStreamReader(process.getErrorStream(), Charset.forName("GBK")));
                    String lineErr;
                    try {
                        while ((lineErr = readerErr.readLine()) != null) {
                            System.out.println(lineErr); // print each line of output
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }).start();
                new Thread(() -> {
                    BufferedReader readerErr = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("GBK")));
                    String lineErr;
                    try {
                        while ((lineErr = readerErr.readLine()) != null) {
                            System.out.println(lineErr); // print each line of output
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }).start();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), Charset.forName("GBK")));

                Scanner sc = new Scanner(System.in);
                while (sc.hasNext()) {
                    bufferedWriter.write(sc.nextLine());
                    bufferedWriter.newLine();

                    bufferedWriter.flush();


                }
                bufferedWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
