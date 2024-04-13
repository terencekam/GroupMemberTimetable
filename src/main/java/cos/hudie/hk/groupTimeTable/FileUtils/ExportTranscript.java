package cos.hudie.hk.groupTimeTable.FileUtils;

import cos.hudie.hk.groupTimeTable.GroupManager.Group;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;

import static cos.hudie.hk.groupTimeTable.FileUtils.Import.ImportFromTXT.FilePath;
import static cos.hudie.hk.groupTimeTable.GroupManager.GroupMemberManager.printTime;
import static cos.hudie.hk.groupTimeTable.Main.DataBaseQuery;

public class ExportTranscript {
    public ExportTranscript(int GroupID) {
        this(DataBaseQuery.getGroup(GroupID));
    }

    public ExportTranscript(Group group) {
        StringBuilder stringBuilder = new StringBuilder();
        group.sortTimeTable();

        stringBuilder.append("GroupINFO\n");
        stringBuilder.append("lecture Name: %s\n".formatted(group.getLectureName()));
        stringBuilder.append("lecture ID : %s\n".formatted(group.getLectureID()));
        stringBuilder.append("TutorGroup : %s\n".formatted(group.getTutorGroup()));
        stringBuilder.append("\n\n");

        stringBuilder.append("All Members Free Time: \n");
        for (int i3 = 0; i3 < DayOfWeek.values().length; i3++) {
            stringBuilder.append(DayOfWeek.values()[i3]).append(": \n");
            for (int i = 0, i2 = 1; i < group.FinalTimeTable.get(i3).size(); i++, i2++) {
                stringBuilder.append("Session ").append(i2).append(": ").append(printTime(group.FinalTimeTable.get(i3).get(i++))).append("-").append(printTime(group.FinalTimeTable.get(i3).get(i))).append("\n");
            }
        }
        stringBuilder.append("\n\n");
        stringBuilder.append("MemberList\n");
        group.StudentList.forEach(stringBuilder::append);

        stringBuilder.append("\n\n");
        stringBuilder.append("MemoList\n");
        group.MemoList.forEach(stringBuilder::append);
        try {
            Path file = Paths.get("outputFiles/transcript.txt");
            Files.writeString(file, stringBuilder);
            System.out.println(FilePath.concat("outputFiles/transcript.txt"));

        } catch (FileNotFoundException e) {
            System.out.println("Creating Directory");
            try {
                new File(FilePath.concat("outputFiles")).mkdirs();
                Path file = Path.of(FilePath.concat("outputFiles/transcript.txt"));
                Files.writeString(file, stringBuilder);
                System.out.println("File Saved In:" + FilePath.concat("outputFiles/transcript.txt"));
            } catch (FileNotFoundException e2) {
                System.out.println(FilePath.concat("outputFiles/transcript.txt"));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.out.println("Output Error with: " + e);
        }
    }
}
