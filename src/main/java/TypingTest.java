import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TypingTest {

    private static String lastInput = "";
    private static Scanner scanner = new Scanner(System.in);
    private static int correctCount = 0;

    public static class InputRunnable implements Runnable {
        @Override
        public void run() {
            lastInput = scanner.nextLine();
        }
    }

    public static void testWord(String wordToTest) throws InterruptedException {
        try {
            System.out.println("Type this: " + wordToTest);
            lastInput = "";

            Thread inputThread = new Thread(new InputRunnable());
            inputThread.start();

            // Wait for the user to finish typing or time out after 10 seconds
            int timeout = 10000; // milliseconds
            inputThread.join(timeout);

            if (inputThread.isAlive()) {
                inputThread.interrupt(); // Input took too long
                System.out.println("\nTime's up!");
            }

            System.out.println("You typed: " + lastInput);
            if (lastInput.equals(wordToTest)) {
                System.out.println("Correct ✅");
                correctCount++;
            } else {
                System.out.println("Incorrect ❌");
            }

            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void typingTest(List<String> inputList) throws InterruptedException {
        correctCount = 0;

        for (String wordToTest : inputList) {
            testWord(wordToTest);
            Thread.sleep(1000); // Brief pause
        }

        System.out.println("Test complete. You got " + correctCount + " out of " + inputList.size() + " correct.");
    }

    public static List<String> loadWordsFromFile(String filePath) {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    words.add(line.trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }
        return words;
    }

    public static void main(String[] args) throws InterruptedException {
        // Load from file (ensure file exists in correct path)
        List<String> words = loadWordsFromFile("resources/Words.txt");

        if (words.isEmpty()) {
            System.out.println("No words found. Using default list.");
            words.add("remember");
            words.add("my friend");
            words.add("boredom");
            words.add("is a");
            words.add("crime");
        }

        typingTest(words);

        System.out.println("Press enter to exit.");
        scanner.nextLine();
    }
}
