package cos.hudie.hk.groupTimeTable;
import cos.hudie.hk.groupTimeTable.Database.ImportGroup;
import cos.hudie.hk.groupTimeTable.Database.Lecture;
import cos.hudie.hk.groupTimeTable.Database.Query;
import cos.hudie.hk.groupTimeTable.FileUtils.ConfigFile;
import cos.hudie.hk.groupTimeTable.FileUtils.ExportToExcel;
import cos.hudie.hk.groupTimeTable.FileUtils.Import.ImportFromTXT;
import cos.hudie.hk.groupTimeTable.GroupManager.Group;
import cos.hudie.hk.groupTimeTable.GroupManager.Memo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static cos.hudie.hk.groupTimeTable.FileUtils.Import.ImportFromTXT.FilePath;

public class Main{
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static boolean log = false;
    public static Query DataBaseQuery;
    public static boolean scannedConfig = false;
    public static StringBuilder logWrite = new StringBuilder();

    public static void main(String[] args) {
        try {
            Path file = Paths.get( FilePath + "log/"  ,"latest.log");
            String FileContent = Files.readString(file);
            String[] configArray = FileContent.split("\n");
            File file1 = new File(FilePath + "log/latest.log");
            int ii;
            for(ii = 1 ; ; ii++){
                Path file2 = Paths.get( FilePath +"log/%s_%d.log".formatted(configArray[configArray.length - 1].substring(1, 11), ii));
                try {
                   Files.readString(file2).isEmpty();
                }catch (Exception ee){
                    file1.renameTo(new File( FilePath +"log/%s_%d.log".formatted(configArray[configArray.length - 1].substring(1, 11), ii)));
                    break;
                }
            }

        }catch (Exception e){
        }
        new ConfigFile();
        Scanner scanner = new Scanner(System.in);
        try {
            int choice;
            do {
                System.out.printf("%-15s %12s", "Lecture Name", "Group ID\n");
                DataBaseQuery.getGroupList().forEach(System.out::println);
                System.out.println("""
                        Group Manager
                        1. add new Group
                        2. edit Group
                        3. delete Group
                        4. list all Group with details
                        5. export all group to excel
                        6. Student Editor
                        7. Edit Lecture
                        8. exit
                        """);
                choice = Integer.parseInt(scanner.next());
                switch (choice) {
                    case 1:

                        System.out.println("Please Enter a Lecture ID");
                        String LectureID = scanner.next();
                        String LectureName = null;
                        if (Main.DataBaseQuery.hasLecture(LectureID)){
                            System.out.println("The system has found a Lecture on the database with LectureID: " + LectureID + " LectureName : " + LectureName);
                            System.out.println("You not need to input the LectureName , The System will auto follow up");
                        }else{
                            System.out.println("Please Enter a Lecture Name");
                            LectureName = scanner.next() + scanner.nextLine();
                        }
                        System.out.println("Please Enter Tutor Group(The Group you are going to present/ Group Leader Tutor Group)");
                        String TutorGroup = scanner.next();
                        Group group = new Group(LectureID, LectureName, TutorGroup);
                        DataBaseQuery.CreateGroup(group);
                        System.out.println("Group Created, now edit the Group Members");
                        System.out.println("Please remember to save before exit the program!");
                        Thread.sleep(2000);
                        group.MemberManager();
                        System.out.printf("Group saved , the groupID is: %s " +
                                        "(You may find this ID by list the Group List function (2))\n"
                                , group.getGroupID());
                        break;
                    case 2:
                        System.out.println("Please enter a GroupID(Your Group ID can be found by 2.list Group Function)," +
                                "\n type 'q' and press enter to exit this screen");
                        String tempInput = scanner.next();
                        int input = 0;
                        try {
                            input = Integer.parseInt(tempInput);
                        } catch (NumberFormatException e) {
                            if (tempInput.equalsIgnoreCase("q")) {
                                break;
                            } else {
                                System.out.println("Wrong Input , Please try again");
                            }
                        }
                        if (DataBaseQuery.hasGroup(input)) {
                            GroupEditor(DataBaseQuery.getGroup(input));
                        } else {
                            System.out.println("There are no such GroupID Please try again");
                        }
                        break;
                    case 3:
                        System.out.println("Please enter a GroupID(Your Group ID can be found by 2.list Group Function)," +
                                "\n type 'q' and press enter to exit this screen");
                        tempInput = scanner.next();
                        input = 0;
                        try {
                            input = Integer.parseInt(tempInput);
                        } catch (NumberFormatException e) {
                            if (tempInput.equalsIgnoreCase("q")) {
                                break;
                            } else {
                                System.out.println("Wrong Input , Please try again");

                            }
                        }
                        if (DataBaseQuery.hasGroup(input)) {
                            DataBaseQuery.deleteGroup(input);
                            System.out.println("Group Deleted");
                        } else {
                            System.out.println("There are no such GroupID Please try again");
                        }
                        break;
                    case 4:
                        List<ImportGroup> list = DataBaseQuery.getGroupList();
                        if (!list.isEmpty()) {
                            list.forEach(e -> System.out.println(DataBaseQuery.getGroup(e.GroupID())));
                        } else {
                            System.out.println("No Group has been added on the System");
                        }
                        break;
                    case 5:
                        list = DataBaseQuery.getGroupList();
                        list.forEach(e -> {
                            try {
                                ExportToExcel.ExportToExcelPerGroup(Main.DataBaseQuery.getGroup(e.GroupID()));
                            } catch (Exception ex) {
                                Main.contactDeveloper(ex);
                            }
                        });
                        break;
                    case 6:
                        EditStudent.StudentEditor();
                        break;
                    case 7:
                        LectureEditor();
                        break;
                    case 8:
                        break;
                    default:
                        System.out.println("Wrong Input , Please try again");
                }
            } while (choice != 8);
        } catch (Exception e) {
            contactDeveloper(e);
        }
        Main.DataBaseQuery.connectionClose();
    }

    public static void GroupEditor(Group group) {
        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        do {
            group = DataBaseQuery.getGroup(group.getGroupID());
            System.out.println(group);
            System.out.println("""
                    Group editor
                    1. edit LectureName
                    2. edit TutorGroup
                    3. edit member
                    4. edit memo
                    5. export to excel
                    6. exit and save
                    """);
            try {
                choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        System.out.println("Please Enter a new LectureName");
                        group.setLectureName(scanner.next() + scanner.nextLine());
                        break;
                    case 2:
                        System.out.println("Please Enter a new TutorGroup");
                        group.setTutorGroup(scanner.next());
                        break;

                    case 3:
                        group.MemberManager();
                        break;
                    case 4:
                        EditMemo(group);
                        break;
                    case 5:
                        ExportToExcel.ExportToExcelPerGroup(group);
                        break;
                    case 6:
                        System.out.println("exiting Group Editor...");
                        Thread.sleep(2000);
                        DataBaseQuery.UpdateGroup(group);
                        break;
                    default:
                        System.out.println("Wrong Input , Please Try Again");
                }
                DataBaseQuery.UpdateGroup(group);
            } catch (NumberFormatException e) {
                System.out.println("Error: " + e);
            } catch (Exception e) {
                contactDeveloper(e);
                choice = -1;
            }
        } while (choice != 6);
    }


    public static String getLocalTime(){
        return "[" + LocalDate.now() + " " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+ "] : ";
    }
    public static void contactDeveloper(Exception e) {
        e.printStackTrace();
        System.out.println(Main.ANSI_RED + "Error: " + e + Main.ANSI_RESET);
        if (Main.log){
            new File("log/");
            try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter("log/latest.log", true));
                    StringBuilder FilesWrite = new StringBuilder();
                    FilesWrite.append(getLocalTime()).append(e).append("\n");
                    for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                        FilesWrite.append(getLocalTime()).append(stackTraceElement.toString() + "\n");
                    }
                    writer.write(String.valueOf(FilesWrite));
                    writer.close();
                }catch (Exception ex){
                    ex.printStackTrace();
            }
        }else if (!Main.scannedConfig) {
            Main.logWrite.append(getLocalTime()).append(e).append("\n");
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                Main.logWrite.append(getLocalTime()).append(stackTraceElement.toString() + "\n");
            }
        }
        System.out.println("If you think this is a bug , please try to contact the developer Github: @TerenceKam");
    }

    public static void AddBack(){
        if(!Main.scannedConfig) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(FilePath + "log/latest.log", true));
                writer.write(String.valueOf(Main.logWrite));
                writer.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void AddINFO(String s){
        if (Main.log) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(FilePath + "log/latest.log", true));
                writer.write(getLocalTime() + s + "\n");
                writer.close();
            } catch (Exception ex) {
                if(Main.log) {
                    new File(FilePath.concat("log")).mkdirs();
                    AddINFO(s);
                }
            }
        }
    }

    public static void EditMemo(Group group) {
        Scanner scanner = new Scanner(System.in);
        int input = 0;
        do {
            group = DataBaseQuery.getGroup(group.getGroupID());
            System.out.printf("%s %6s %18s %5s %15s %10s", "Index", "Memo Name", "Starting Time", "->", "Ending Time", "Description\n");
            group.MemoList.forEach(System.out::println);
            System.out.println("""
                    Memo Manager
                    1. add a Memo
                    2. edit a Memo
                    3. delete a Memo
                    4. exit
                    """);
            try {
                String tempInput = scanner.next();
                input = Integer.parseInt(tempInput);
                switch (input) {
                    case 1:
                        System.out.println("Please input a Memo Name");
                        String memoName = scanner.next() + scanner.nextLine();
                        LocalDateTime StartingTime = localDateTimeInput("Starting Time");
                        LocalDateTime EndingTime = localDateTimeInput("Ending Time");
                        if (StartingTime.isAfter(EndingTime)) {
                            System.out.println("Wrong Input, Your input of Ending Time is early then your Starting Time");
                            System.out.println("Please Input again...back to Memo Manager now");
                            Thread.sleep(2000);
                        } else {
                            System.out.println("Please input a memo description");
                            String memoDescription = scanner.next() + scanner.nextLine();
                            DataBaseQuery.addMemo(group, new Memo(memoName, StartingTime, EndingTime, memoDescription));
                            System.out.println("Memo added");
                        }
                        break;
                    case 2:
                        String tempSelector;
                        Memo memo = null;
                        try {
                            do {
                                System.out.println("Please input the Memo Index (listed on left hand side)");
                                String indexTemp = scanner.next();
                                int index = Integer.parseInt(indexTemp);
                                if (Main.DataBaseQuery.hasMemo(index)) {
                                    memo = Main.DataBaseQuery.getMemo(index);
                                    break;
                                } else if (index == -1) {
                                    break;
                                } else {
                                    System.out.println("There are no such Index , Please try again (-1 to exit)");
                                }
                            } while (true);
                            int selector;
                            do {
                                System.out.printf("%-3s %10s %10s %10s %10s\n", "Index", "Memo Name", "Starting Time", "Ending Time", "Description");
                                System.out.println(memo);
                                System.out.println("""
                                        1. Edit Name;
                                        2. Edit StartTime
                                        3. Edit EndTime
                                        4. Edit Description
                                        5. Save and Exit
                                        """);
                                tempSelector = scanner.next();
                                selector = Integer.parseInt(tempSelector);
                                switch (selector) {
                                    case 1:
                                        System.out.printf("Edit a new name (old name: %s) :", memo.getName());
                                        memo.setName(scanner.next());
                                        break;
                                    case 2:
                                        System.out.printf("Edit a new Start Time (old Start Time: %s) :", memo.getStartTime().toLocalDate() + " " + memo.getStartTime().toLocalTime());
                                        memo.setStartTime(localDateTimeInput("Start Time"));
                                        break;
                                    case 3:
                                        System.out.printf("Edit a new End Time (old End Time: %s) :", memo.getEndTime().toLocalDate() + " " + memo.getEndTime().toLocalTime());
                                        memo.setEndTime(localDateTimeInput("End Time"));
                                        break;
                                    case 4:
                                        System.out.printf("Edit a description (old description: %s) :", memo.getDescription());
                                        memo.setDescription(scanner.next()+ scanner.nextLine());
                                        break;
                                    case 5:
                                        System.out.println("Saving...");
                                        DataBaseQuery.editMemo(group, memo);
                                        break;
                                    default:
                                        System.out.println("Wrong Input");
                                }
                            } while (selector != 5);
                        } catch (Exception e) {
                            Main.contactDeveloper(e);
                        }

                        break;
                    case 3:
                        System.out.println("Please input the Memo Index (listed on left hand side)");
                        int index = scanner.nextInt();
                        if (DataBaseQuery.hasMemo(index)) {
                            DataBaseQuery.deleteMemo(index);
                        } else {
                            System.out.println("There are no such index , Please try again");
                        }
                        break;
                    case 4:
                        break;
                    default:
                        System.out.println("Wrong Number Input , Please try again");
                }
            } catch (NumberFormatException e) {
                System.out.println("Wrong Type Input, Please try again");
            } catch (Exception e) {
                Main.contactDeveloper(e);
            }
        } while (input != 4);
    }

    public static LocalDateTime localDateTimeInput(String TimeName) {
        String Date;
        String Time;
        while (true) {
            System.out.printf("Please input Date of %s (Format YYYY-MM-DD) : ", TimeName);
            Scanner scanner = new Scanner(System.in);
            Date = scanner.next();
            List<Character> DateArray = new ArrayList<>();
            for (char c : Date.toCharArray()) {
                DateArray.add(c);
            }

            if (DateArray.get(4) == '-' && '-' == DateArray.get(7)) {
                DateArray.remove(4);
                DateArray.remove(6);
                try {
                    for (Character c : DateArray) {
                        Integer.parseInt(c.toString());
                    }
                    if ((!LocalDate.now().isBefore(LocalDate.parse(Date)))&&(!LocalDate.now().isEqual(LocalDate.parse(Date)))) {
                        System.out.println("The Date Time is before current");
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    Main.contactDeveloper(e);
                }
            } else {
                System.out.println("Wrong Date Input... Please try again");
            }
        }
        while (true) {
            System.out.printf("Please input Time of %s (Format HH:MM:SS) : ", TimeName);
            Scanner scanner = new Scanner(System.in);
            Time = scanner.next();
            List<Character> TimeArray = new ArrayList<>();
            for (char c : Time.toCharArray()) {
                TimeArray.add(c);
            }

            if (TimeArray.get(2) == ':' && ':' == TimeArray.get(5)) {
                TimeArray.remove(2);
                TimeArray.remove(4);
                try {
                    for (Character c : TimeArray) {
                        Integer.parseInt(c.toString());
                    }
                    break;
                } catch (Exception e) {
                    Main.contactDeveloper(e);
                }
            } else {
                System.out.println("Wrong Time Input... Please try again");
            }
        }
        return LocalDateTime.parse(Date + "T" + Time);
    }

    public static void LectureEditor(){
        Scanner scanner = new Scanner(System.in);
        String tempSelect;
        int select;
        try{
            do{
                System.out.println("%10s %20s" .formatted( "LectureID" , "LectureName"));
                Main.DataBaseQuery.getLectureList().forEach(System.out::println);
                System.out.println("""
                Lecture Editor
                **LectureID cannot be edit , You may delete a Lecture to create a new one**
                **Lecture delete will remove all related groups!**
                
                1. Edit a LectureName
                2. Create a new Lecture
                3. Delete Lecture
                4. exit
                """);
                System.out.print("Please Select: ");
                tempSelect = scanner.next();
                select = Integer.parseInt(tempSelect);
                switch (select){
                    case 1:
                        System.out.print("Please enter the LectureID: ");
                        String LectureID = scanner.next();
                        String LectureName;
                        if (Main.DataBaseQuery.hasLecture(LectureID)){
                            System.out.print("Please enter a new LectureName: ");
                            LectureName = scanner.next() + scanner.nextLine();
                            Main.DataBaseQuery.UpdateLecture(new Lecture(LectureID , LectureName));
                            System.out.println("Lecture Updated");
                        }else {
                            System.out.println("The system can't found a Lecture with LectureID : %s".formatted(LectureID));
                        }
                        break;
                    case 2:
                        System.out.print("Please enter a new LectureID: ");
                        LectureID = scanner.next();
                        if (!Main.DataBaseQuery.hasLecture(LectureID)){
                            System.out.print("Please enter a new LectureName: ");
                            LectureName = scanner.next() + scanner.nextLine();
                            Main.DataBaseQuery.CreateLecture(new Lecture(LectureID , LectureName));
                            System.out.println("Lecture Created");
                        }else {
                            System.out.println("The system has found a Lecture with LectureID : %s with LectureName : %s".formatted(LectureID , Main.DataBaseQuery.getLectureName(LectureID)));
                        }
                        break;
                    case 3:
                        System.out.print("Please enter the LectureID: ");
                        LectureID = scanner.next();
                        String select2 = "";
                        do {
                            if (Main.DataBaseQuery.hasLecture(LectureID)){
                                System.out.println("%sWARNING! All the group with this LectureID will be deleted  , are you sure to continue? (y/n)%s".formatted(Main.ANSI_RED, Main.ANSI_RESET));
                                select2 = scanner.next();
                                if (select2.equalsIgnoreCase("y")){
                                    Main.DataBaseQuery.DeleteLecture(LectureID);
                                    break;
                                }else if (select2.equalsIgnoreCase("n")){
                                    break;
                                }else{
                                    System.out.println("Wrong Input! please try again");
                                }
                            }
                        }while (!select2.equalsIgnoreCase("n"));
                        break;
                    case 4:
                        break;
                    default:
                        System.out.println("Wrong select , please try again");

                }
            }while (select!=4);
        }catch (Exception e){
            Main.contactDeveloper(e);
        }

    }

}
