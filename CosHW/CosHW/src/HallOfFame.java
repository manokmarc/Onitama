import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HallOfFame {
    private static final String fileName = "HallOfFame.txt";
    private final List<List<String>> entries = new ArrayList<>();

    HallOfFame() {
        // decode the file and put each entry score in a list
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            int lineCount = 0;
            List<String> entry = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) { // read each line
                entry.add(line); // add each line to the entry
                lineCount++;
                if (lineCount > 2) { // when 3 lines is aquired add the entry to entries and clear the data
                    entries.add(new ArrayList<>(entry)); // add data aquired
                    entry.clear(); // clear current data
                    lineCount = 0;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("HallOfFame.txt not found");
        } catch (IOException e) {
            System.out.println("Something went wrong");
        }
    }

    void saveScore(List<String> names, int moveCount) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // cheks if score aquired is good enough to be put in the hall of fame
            int placement = 0; // keeps track of what placement we are in
            List<String> newEntry = new ArrayList<>(names); // list containing the new entry data
            newEntry.add(Integer.toString(moveCount));

            for (List<String> entry : entries) {
                if (Integer.parseInt(entry.getLast()) >= moveCount) { // checks if the new entry move count is lower than current entry checked
                    entries.add(placement, new ArrayList<>(newEntry));
                    break;
                }
                placement++;
            }
            if (entries.isEmpty()) { // automatically adds the new entry if there is no prior entry's
                entries.add(placement, new ArrayList<>(newEntry));
            }
            if (entries.size() > 10) { // ensures that there are only 10 max entry's
                entries.removeLast();
            }
            for (List<String> entry : entries) { // encodes each value in the list of entry's to the actual file
                for (String value : entry) { // put each value of the entry a separate line
                    writer.write(value);
                    writer.newLine();
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("HallOfFame.txt not found");
        } catch (IOException e) {
            System.out.println("Something went wrong");
        }
    }

    void printScores() {
        int position = 1; // keeps track of current position
        System.out.println("★★★ HALL OF FAME ★★★");
        for (List<String> entry : entries) { // display each entry
            System.out.println(position + ". " + entry.subList(0, 2) + "  ♟︎" + entry.getLast());
            position++;
        }
    }
}
