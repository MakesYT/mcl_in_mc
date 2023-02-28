package top.ncserver.mclmc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public final class MclMC extends JavaPlugin {
    public  Process process;
    public BufferedWriter bufferedWriter;
    public static String charset="UTF-8";
    public static MclMC INSTANCE ;
    public static void copyFile(InputStream inputStream, File file) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] arrayOfByte = new byte[63];
            int i;
            while ((i = inputStream.read(arrayOfByte)) > 0) {
                fileOutputStream.write(arrayOfByte, 0, i);
            }
            fileOutputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onEnable() {
        INSTANCE=this;
        Bukkit.getPluginCommand("mcl").setExecutor(new Command());
        String os = System.getProperty("os.name");
        if (os != null && os.toLowerCase().startsWith("win")) {
            // Windows操作系统
            charset="GBK";
            getLogger().info("GBK");
        }

        File configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            copyFile(this.getResource("config.yml"), configFile);

            this.getLogger().info("已生成 config.yml 文件");
        }

        File mclJar = new File(this.getDataFolder(), "mcl.jar");
        if (!mclJar.exists()) {
            mclJar.getParentFile().mkdirs();
            getLogger().info("正在获取Mcl...");
            try {
                URL url = new URL("https://mirai.mamoe.net/assets/mcl/org/itxtech/mcl/package.json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                connection.disconnect();
                Gson gson =new  Gson();
                JsonObject jsonObject =gson.fromJson(sb.toString(), JsonObject.class);
                JsonArray stables= jsonObject.getAsJsonObject("channels").getAsJsonArray("stable");
                String downloadUrl = jsonObject.getAsJsonObject("repo")
                        .getAsJsonObject(stables.get(stables.size() - 1).getAsString())
                        .get("archive").getAsString();
                if (charset.equals("GBK")){
                    getLogger().info(this.getDataFolder().getAbsolutePath()+"\\temp.zip");
                    Tools.downloadFile(downloadUrl,this.getDataFolder().getAbsolutePath()+"\\temp.zip");
                    Tools.unzipMclJar(this.getDataFolder().getAbsolutePath()+"\\temp.zip",this.getDataFolder().getAbsolutePath());
                }else {
                    getLogger().info(this.getDataFolder().getAbsolutePath()+"/temp.zip");
                    Tools.downloadFile(downloadUrl,this.getDataFolder().getAbsolutePath()+"/temp.zip");
                    Tools.unzipMclJar(this.getDataFolder().getAbsolutePath()+"/temp.zip",this.getDataFolder().getAbsolutePath());
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            this.getLogger().info("已下载mcl.jar文件");
        }
        runMcl();






    }
    private boolean isClosed=false;
    @Override
    public void onDisable() {


        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(30000);
                    if (!isClosed){
                        getLogger().info("进程关闭超时,强制终止");
                        process.destroy();
                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
        try {
            getLogger().info("正在关闭进程");
            MclMC.INSTANCE.bufferedWriter.write("stop");
            MclMC.INSTANCE.bufferedWriter.newLine();
            MclMC.INSTANCE.bufferedWriter.flush();
            process.waitFor();
            isClosed=true;
        }catch (Exception e){

        }
    }
    public void runMcl(){
        File runDir=new File(Bukkit.getPluginManager().getPlugin("MclMC").getDataFolder().toURI());
        new BukkitRunnable(){
            @Override
            public void run() {
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.directory(runDir);
                processBuilder.redirectErrorStream(true);
                File configFile = new File(Bukkit.getPluginManager().getPlugin("MclMC").getDataFolder(), "config.yml");
                YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(configFile);
                if (charset.equals("GBK"))
                    processBuilder.command(System.getProperty("java.home")+"\\bin\\java", yamlConfiguration.getString("args"),"-jar","mcl.jar");
                else
                    processBuilder.command(System.getProperty("java.home")+"/bin/java",yamlConfiguration.getString("args"),"-jar","mcl.jar");
                try {
                    process = processBuilder.start();

                    InputStream in = process.getInputStream();
                     bufferedWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(),Charset.forName(charset)));

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName(charset)));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        Bukkit.getPluginManager().getPlugin("MclMC").getLogger().info(line); // print each line of output
                    }
                    Bukkit.getPluginManager().getPlugin("MclMC").getLogger().info("进程关闭");

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("MclMC"));
    }
}
