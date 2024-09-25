package cos.hudie.hk.groupTimeTable.FileUtils.Import;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static cos.hudie.hk.groupTimeTable.FileUtils.Import.ImportFromTXT.FilePath;

public class WebPaste {
    public static List<List<Integer>> WebPaste() {
        String FileContent;
        String filepath = "WebPaste/";
        System.out.println(filepath);
        new File(filepath).mkdirs();
        System.out.printf("Please place the file import.txt under %s directory and type \"ok\" \n OR specify the .txt file (e.g. import.txt ) " +
                        "and place the under %s directory : \n",
                filepath, filepath);
        String tempString = new Scanner(System.in).next();
        try {
            Path file = Path.of(filepath.concat((tempString.equalsIgnoreCase("ok")) ? "import.txt" : tempString));
            FileContent = Files.readString(file);
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
        List<String> list = List.of(FileContent.split("\r\n"));
        String s = list.get(1);
        System.out.print(s);
        List<List<Integer>> tempList = new ArrayList<>();
        List<Integer> tempSubList = new ArrayList<>();
        int i = 0;
        do {

            if (IsDayOfWeek(list.get(i))) {
                for (DayOfWeek value : DayOfWeek.values()) {
                    if (value.name().equalsIgnoreCase(list.get(i))) {
                        i++;
                        do {
                            if (i < list.size()) {
                                try {
                                    tempString = list.get(i).replace(":", "");
                                    tempString = tempString.replace(" ", "");
                                    tempString = tempString.replace("-", "");
                                    tempSubList.add(Integer.parseInt(tempString.substring(0, 4)));
                                    tempSubList.add(Integer.parseInt(tempString.substring(4, 8)));
                                    i += 4;
                                    System.out.println(tempSubList);
                                }catch (Exception e){
                                    break;
                                }
                            } else {
                                break;
                            }

                        } while (!IsDayOfWeek(list.get(i)));
                        tempList.add(new ArrayList<>(tempSubList));
                        tempSubList.clear();//
                    } else {
                        tempList.add(new ArrayList<>());
                    }
                    if (i >= list.size() - 1) {
                        break;
                    }
                }
            }
            i++;
        } while (i < list.size() - 1);
        System.out.println(tempList);
        System.out.println("Import successful");
        return tempList;
    }


    public static boolean IsDayOfWeek(String s) {
        for (DayOfWeek value : DayOfWeek.values()) {
            if (value.name().equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }
}
