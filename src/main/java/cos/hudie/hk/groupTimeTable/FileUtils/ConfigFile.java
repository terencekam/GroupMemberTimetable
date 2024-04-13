package cos.hudie.hk.groupTimeTable.FileUtils;

import cos.hudie.hk.groupTimeTable.Main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static cos.hudie.hk.groupTimeTable.FileUtils.Import.ImportFromTXT.FilePath;
import static cos.hudie.hk.groupTimeTable.Main.AddBack;

public class ConfigFile {

    public static void End(String s){
        System.out.println(s);
        System.exit(0);
    }

    public ConfigFile() {
        List<String> FinalconfigList = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        try {
            Path file = Paths.get("config.yml");
            String FileContent = Files.readString(file);
            String[] configArray = FileContent.split("\n");
            boolean config = false;
            for (String s : configArray) {
                for (char c : s.toCharArray()) {
                    if(c == '#'){
                        break;
                    }else{
                        config = true;
                    }
                }
                if (config) {
                    FinalconfigList.add(s.substring(s.indexOf("\"")+1, s.indexOf("\"", s.indexOf("\"") + 1)));
                    config = false;
                }
            }
            switch (FinalconfigList.get(0)){
                case "MySQL":
                    Main.DataBaseQuery = new cos.hudie.hk.groupTimeTable.Database.MySQL.Query(
                            FinalconfigList.get(1) ,   Integer.parseInt(FinalconfigList.get(2)) ,
                            FinalconfigList.get(3) , FinalconfigList.get(4) ,
                            FinalconfigList.get(5) , Boolean.parseBoolean(FinalconfigList.get(6)));
                    break;
                case "SQLite":
                    Main.DataBaseQuery = new cos.hudie.hk.groupTimeTable.Database.SQLite.Query();
                    break;
                case "H2":
                    Main.DataBaseQuery = new cos.hudie.hk.groupTimeTable.Database.H2.Query();
                    break;
                case "":
                    End("Since no database set , exiting...");
                default:
                    System.out.println("Please remove the config.yml and try again");
                    End("Exiting");
            }

        } catch (IOException e) {
            e.printStackTrace();
            FileOutputStream fileOutputStream;
            try {
                stringBuilder.append("#The ConnectionType support {'H2' , 'MySQL' , 'SQLite'}\n");
                stringBuilder.append("#No setup for H2 , SQLite is needed as the database will setup on the filepath of the Main .jar file\n");
                stringBuilder.append("#If you selected MySQL as your database , please config this File\n");
                stringBuilder.append("#Do not change the default settings unless you know what you are doing!\n");
                stringBuilder.append("\n\n");
                stringBuilder.append("ConnectionType: \"SQLite\"\n");
                stringBuilder.append("\n\n");
                stringBuilder.append("#MySQL section\n");
                stringBuilder.append("IPAddress: \"\"\n");
                stringBuilder.append("Port: \"3306\"\n");
                stringBuilder.append("username: \"\"\n");
                stringBuilder.append("password: \"\"\n");
                stringBuilder.append("DataBase: \"Members\"\n");
                stringBuilder.append("SSL : \"false\"\n");
                stringBuilder.append("\n\n");
                stringBuilder.append("version: \"1.0\"\n");
                stringBuilder.append("log: \"false\"\n");
                fileOutputStream = new FileOutputStream(new File("config.yml"));
                BufferedWriter write = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
                write.write(stringBuilder.toString());
                write.close();
                fileOutputStream.close();
                System.out.println("The config file has been set, please config the config file");
                System.out.println(Main.ANSI_CYAN + FilePath.concat("config.yml")+Main.ANSI_RESET);
                End("");
            } catch (Exception s) {
                s.printStackTrace();
                System.out.println("Please remove the config.yml and try again");
                Main.contactDeveloper(s);
                End("Exiting");
            }
        } catch (Exception s) {
        System.out.println(Main.ANSI_RED + "Error! " + "The config wrongly set .Please remove the config.yml and try again" + Main.ANSI_RESET);
            try {
                Path file = Path.of(FilePath.concat("config.yml"));
                String FileContent = Files.readString(file);
                Main.logWrite.append("\n\n\n\n\nConfig File : \n").append(FileContent).append("\n\n\n\n\n");
                Main.contactDeveloper(s);
                End("Exiting...");
            }catch (Exception e){
                Main.contactDeveloper(e);
            }
        }
        if(FinalconfigList.get(8).equalsIgnoreCase("true")){
            Main.log = true;
            Main.scannedConfig = true;
            AddBack();
            Main.logWrite = null;
        }
    }
}
