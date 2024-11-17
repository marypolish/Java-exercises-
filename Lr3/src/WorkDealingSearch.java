import java.util.concurrent.*;
import java.util.Scanner;

public class WorkDealingSearch {
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введіть кількість рядків масиву: ");
        int rows = validateInput(scanner);
        System.out.print("Введіть кількість стовпців масиву: ");
        int cols = validateInput(scanner);

        int[][] array = generateArray(rows, cols);
        System.out.println("Згенерований масив:");
        printArray(array);

        System.out.println("Початок обчислень...");
        long startTime = System.nanoTime();
        Integer result = findMatchingElement(array);
        long endTime = System.nanoTime();

        System.out.println(result != null
                ? "Знайдено елемент: " + result
                : "Елемент не знайдено.");
        System.out.println("Час виконання: " + (endTime - startTime) / 1_000_000 + " мс");
        executor.shutdown();
    }

    private static Integer findMatchingElement(int[][] array) {
        CountDownLatch latch = new CountDownLatch(array.length);
        Integer[] result = {null};

        for (int i = 0; i < array.length; i++) {
            int row = i;
            executor.submit(() -> {
                for (int j = 0; j < array[row].length; j++) {
                    System.out.println("Перевірка елементу: array[" + row + "][" + j + "] = " + array[row][j] + ", сума індексів = " + (row + j));
                    if (array[row][j] == row + j) {
                        synchronized (result) {
                            result[0] = array[row][j];
                        }
                        break;
                    }
                }
                latch.countDown();
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result[0];
    }

    private static int[][] generateArray(int rows, int cols) {
        int[][] array = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                array[i][j] = (int) (Math.random() * 100);
            }
        }
        return array;
    }

    private static void printArray(int[][] array) {
        for (int[] row : array) {
            for (int value : row) {
                System.out.print(value + "\t");
            }
            System.out.println();
        }
    }

    private static int validateInput(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.print("Введіть коректне ціле число: ");
            scanner.next();
        }
        return scanner.nextInt();
    }
}
