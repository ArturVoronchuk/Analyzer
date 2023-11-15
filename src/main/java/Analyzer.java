import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Analyzer {

    public static BlockingQueue<String> charA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> charB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> charC = new ArrayBlockingQueue<>(100);

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    private static int maxNumbsOfChars(BlockingQueue<String> queue, char letter) {
        int count = 0;
        int max = 0;
        String text;
        try {
            for (int i = 0; i < 10_000; i++) {
                text = queue.take();
                for (char c : text.toCharArray()) {
                    if (c == letter) count++;
                }
                if (count > max) max = count;
                count = 0;
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " поток прерван");
            return -1;
        }
        return max;
    }

    public static void main(String[] args) throws Exception {

        Thread generateText = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                String text = generateText("abc",100_000);
                try{
                    charA.put(text);
                    charB.put(text);
                    charC.put(text);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        generateText.start();

        Thread a = new Thread(() -> {   //запускаем три потока, каждый из которых ищет свой символ
            char letter = 'a';
            int maxA = maxNumbsOfChars(charA, letter);
            System.out.println("Максимальное количество символов " + letter + " : " + maxA);
        });
        a.start();

        Thread b = new Thread(() -> {
            char letter = 'b';
            int maxB = maxNumbsOfChars(charB, letter);
            System.out.println("Максимальное количество символов " + letter + " : " + maxB);
        });
        b.start();

        Thread c = new Thread(() -> {
            char letter = 'c';
            int maxC = maxNumbsOfChars(charC, letter);
            System.out.println("Максимальное количество символов " + letter + " : " + maxC);
        });
        c.start();

        a.join();
        b.join();
        c.join();
    }
}