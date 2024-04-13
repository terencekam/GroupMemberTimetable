package cos.hudie.hk.groupTimeTable.GroupManager;


import cos.hudie.hk.groupTimeTable.EditStudent;
import cos.hudie.hk.groupTimeTable.FileUtils.ExportTranscript;
import cos.hudie.hk.groupTimeTable.FileUtils.Import.WebPaste;
import cos.hudie.hk.groupTimeTable.Main;
import cos.hudie.hk.groupTimeTable.Student;

import java.time.DayOfWeek;
import java.util.*;


public class GroupMemberManager {
    public LinkedList<Integer> TimeList = new LinkedList<>();
    public List<Student> StudentList = new ArrayList<>();
    public List<List<Integer>> FinalTimeTable = new ArrayList<>();
    protected int GroupID;

    public static boolean IsCorrectTimeTable(List<Integer> TimeTable) {
        for (int i = 0; i < TimeTable.size(); i++) {
            if (TimeTable.get(i++) > TimeTable.get(i)) {
                return false;
            }
        }
        return true;
    }

    public static String printTime(int Time) {
        if (Time < 10) {
            return "000" + Time;
        } else if (Time < 100) {
            return "00" + Time;
        } else if (Time < 1000) {
            return "0" + Time;
        }
        return String.valueOf(Time);
    }

    public void MemberManager(){
        int input = 0;
        do {
            Group TempGroup = Main.DataBaseQuery.getGroup(GroupID);
            StudentList = TempGroup.StudentList;
            System.out.println("""
                    1. add new Student
                    2. edit student
                    3. Delete a student
                    4. search student profile
                    5. print all student profile
                    6. get Time Table
                    7. export transcript
                    8. save and exit
                    """);
            try {
                Scanner scanner = new Scanner(System.in);
                String tempInput = scanner.next();
                input = Integer.parseInt(tempInput);
                switch (input) {
                    case 1:
                        StudentInitialisation();
                        break;
                    case 2:
                        System.out.println("Input a StudentID to edit:");
                        String SID = new Scanner(System.in).next();
                        if (Main.DataBaseQuery.hasGroupMember(SID, GroupID)) {
                            EditStudent.EditStudent(GroupID, SID);
                        } else {
                            System.out.println("Student doesn't exist in this group");
                        }
                        break;
                    case 3:
                        System.out.println("Input a StudentID to remove:");
                        SID = new Scanner(System.in).next();
                        if (Main.DataBaseQuery.hasGroupMember(SID, GroupID)) {
                            Main.DataBaseQuery.deleteGroupMember(GroupID, SID);
                            System.out.println("Student Deleted (This Student is still in this database , but no longer link to this group , you may delete this Student on Student Editor)");
                        } else {
                            System.out.println("Student not found");
                        }
                        break;
                    case 4:
                        printStudent();
                        break;
                    case 5:
                        StudentList.forEach(System.out::println);
                        break;
                    case 6:
                        sortTimeTable();
                        System.out.println("All Members Free Time: ");
                        StringBuilder TimeSetString = new StringBuilder();
                        for (int i3 = 0; i3 < DayOfWeek.values().length; i3++) {
                            TimeSetString.append(DayOfWeek.values()[i3]).append(": \n");
                            for (int i = 0, i2 = 1; i < FinalTimeTable.get(i3).size(); i++, i2++) {
                                TimeSetString.append("Session ").append(i2).append(": ").append(printTime(FinalTimeTable.get(i3).get(i++))).append("-").append(printTime(FinalTimeTable.get(i3).get(i))).append("\n");
                            }
                        }
                        System.out.println(TimeSetString);
                        break;
                    case 7:
                        new ExportTranscript(GroupID);
                        break;
                    case 8:
                        break;
                    default:
                        System.out.println("Wrong Input");
                }
            } catch (Exception e) {
                Main.contactDeveloper(e);
            }
        }while (input != 8 );
    }

    public void initialisation() {
        TimeList.add(0);
        TimeList.add(2330);
    }

    public void TimeReduce(int StartTime, int EndTime) {
        var StartResult = Collections.binarySearch(TimeList, StartTime);
        var EndResult = Collections.binarySearch(TimeList, EndTime);
        if (StartResult < 0) {
            if ((-StartResult - 1) % 2 == 0) {
                StartResult = -StartResult - 2;
                StartTime = TimeList.get(StartResult);
            }
        } else {
            if (StartResult % 2 == 0) {
                StartTime = TimeList.get(StartResult - 1);
            }
        }
        if (EndResult < 0) {
            if ((-EndResult - 1) % 2 == 0) { //within time available range
                EndResult = -EndResult - 1;
                EndTime = TimeList.get(EndResult);
            }
        } else {
            if (EndResult % 2 == 0) {
                EndTime = TimeList.get(EndResult);
            } else {
                EndTime = TimeList.get(EndResult + 1);
            }
        }


        if (!(Collections.binarySearch(TimeList, StartTime) > 0)) {
            TimeList.add(StartTime);
            Collections.sort(TimeList);
        }

        if (!(Collections.binarySearch(TimeList, EndTime) > 0)) {
            TimeList.add(EndTime);
            Collections.sort(TimeList);
        }

        boolean isDone = false;
        while (!isDone) {
            for (Integer i : TimeList) {
                if ((i > StartTime) && (i < EndTime)) {
                    TimeList.remove(Collections.binarySearch(TimeList, i));
                    isDone = false;
                    break;
                }
                isDone = true;
            }

        }
    }

    @Override
    public String toString() {
        return "GroupMemberManager{" +
                "TimeList=" + TimeList +
                ", StudentList=" + StudentList +
                ", FinalTimeTable=" + FinalTimeTable +
                '}';
    }

    public void StudentInitialisation() {

        Scanner scanner = new Scanner(System.in);

        String accept;
        String SID = "";
        do {
            System.out.println("Please Input Student ID");
            SID = scanner.next();
            System.out.printf("Please confirm the SID : %s %s  \n" +
                    "%s Warning! Student ID Cannot edit unless you remove the old student and make a new one \n " +
                    "%sConfirm Please type 'y' or press any key to re-enter: ", Main.ANSI_CYAN, SID, Main.ANSI_RED, Main.ANSI_RESET);
            accept = scanner.next();
        } while (!accept.equalsIgnoreCase("y"));
        if (Main.DataBaseQuery.hasStudent(SID)) {
            if (Main.DataBaseQuery.hasGroupMember(SID, GroupID)) {
                System.out.printf(" %s This Student has added to Group already! %s\n", Main.ANSI_RED , Main.ANSI_RESET);
            } else {
                System.out.printf("The System has found a record with %s , linked to this group \n You don't need to input any information as it has been added from the database\n", SID);
                Main.DataBaseQuery.addGroupMember(GroupID, Main.DataBaseQuery.getStudent(SID));
            }

        } else {
            System.out.println("Please Input Student Name: ");
            var SName = scanner.next() + scanner.nextLine();
            var thisStudent = new Student(SName, SID);
            var input = 0;
            while (input != 1 && input!=2) {
                try {
                    System.out.println("""
                            Please select the TimeTable input method
                            1. input by typing
                            2. input by webPaste (If you don't know how to do so, Please find the docs)
                            """);
                    var tempInput = scanner.next();
                    input = Integer.parseInt(tempInput);

                    switch (input) {
                        case 1:
                            for (int i2 = 0; i2 < DayOfWeek.values().length; i2++) {
                                var TimeSet = new ArrayList<Integer>();
                                System.out.printf("Input %s 's student Time Table Session, Finish by \"e\" or Skip All by \"SkipAll\": \n (%s)", SName, DayOfWeek.values()[i2]);
                                int i = 1;
                                String temp;
                                do {
                                    System.out.printf("Session %s [Start Time , End Time ]: ", i);
                                    i++;
                                    temp = scanner.next();
                                    int StartTime;
                                    if ((!temp.equalsIgnoreCase("e")) && (!temp.equalsIgnoreCase("SkipAll"))) {
                                        try {
                                            StartTime = Integer.parseInt(temp);
                                            var EndTime = scanner.nextInt();
                                            if(EndTime>=2330||StartTime<=30){
                                                System.out.println("The Input Time need to be in range 0030 - 2330");
                                            }else if ((EndTime%100 != 30 && EndTime%100 != 0) || (StartTime%100 != 30 && StartTime%100 != 0)) {
                                                System.out.println("The minutes should be 30 or 00");
                                            }else {
                                                if (StartTime < EndTime) {
                                                    TimeSet.add(StartTime);
                                                    TimeSet.add(EndTime);
                                                } else {
                                                    System.out.println("The session you input is incorrect since the Start Time >= End Time , Please input again");
                                                    i--;
                                                }
                                            }
                                        } catch (Exception e) {
                                            System.out.println("Wrong input: " + e);
                                            i--;
                                        }

                                    }

                                } while (!temp.equalsIgnoreCase("e") && !temp.equalsIgnoreCase("SkipAll"));
                                thisStudent.TimeTable.add(TimeSet);
                                if (temp.equalsIgnoreCase("SkipAll")) {
                                    for (int i3 = 0; i3 < (DayOfWeek.values().length - i2 - 1); i3++) {
                                        thisStudent.TimeTable.add(new ArrayList<>());
                                    }
                                    break;
                                }
                            }
                            Main.DataBaseQuery.addStudent(thisStudent);
                            Main.DataBaseQuery.addGroupMember(GroupID, thisStudent);
                            break;
                        case 2:
                            thisStudent.TimeTable = WebPaste.WebPaste();
                            Main.DataBaseQuery.addStudent(thisStudent);
                            Main.DataBaseQuery.addGroupMember(GroupID, thisStudent);
                            break;
                        default:
                            System.out.println("Error: Wrong Input");
                    }

                } catch (Exception e) {
                    Main.contactDeveloper(e);
                }
            }

        }
    }

    public void sortTimeTable() {
        FinalTimeTable.clear();
        for (int i2 = 0; i2 < 7; i2++) {
            initialisation();
            for (Student student : StudentList) {
                for (int i = 0; i < student.TimeTable.get(i2).size(); i++) {
                    TimeReduce(student.TimeTable.get(i2).get(i++), student.TimeTable.get(i2).get(i));
                }
            }
            FinalTimeTable.add(new ArrayList<>(TimeList.stream().toList()));
            TimeList.clear();
        }
    }

    public void printStudent() {
        System.out.println("Input a StudentID or StudentName : ");
        Scanner scanner = new Scanner(System.in);
        String search = scanner.next();
        StudentList.forEach(e -> e.printStudent(search));
    }

}
