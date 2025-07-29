import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Card {
    private final String filePath;
    String name;
    String color;
    List<List<Integer>> moves = new ArrayList<>();

    Card(String fileName) {
        filePath = "MoveCards/" + fileName + ".txt"; // set up file path location
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            name = fileName;
            color = reader.readLine(); // get the color and skips it

            // get the possible moves the card can do with "X" as the relative center
            int row = 5; // start row at 5
            String line;
            while ((line = reader.readLine()) != null) { // loop through each line and each character in the file
                int column = 1; // start colum a 1
                for (char c : line.toCharArray()) {
                    if (c == 'x') { // looks for the x positons in the grid
                        moves.add(Arrays.asList(column - 3, row - 3)); // get the relative move positions by subtracting the position to the center of the grid
                    }
                    column++;
                }
                row--;
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Something went wrong");
        }
    }

    void printCardMovePattern() { // display the cards move patterns
        try (BufferedReader reader = new BufferedReader(new FileReader(this.filePath))) {
            String line;
            reader.readLine(); // skip the card colour
            while ((line = reader.readLine()) != null) { // print each line in the file
                System.out.println(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Something went wrong");
        }
    }
}

