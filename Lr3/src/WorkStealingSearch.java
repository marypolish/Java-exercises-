import java.util.concurrent.*;
import java.util.Scanner;

public class WorkStealingSearch {
    private static final ForkJoinPool pool = new ForkJoinPool();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введіть кількість рядків масиву: ");
        int rows = validateInput(scanner);
        System.out.print("Введіть кількість стовпців масиву: ");
        int cols = validateInput(scanner);

        int[][] array = generateArray(rows, cols);
        printArray(array);

        System.out.println("Початок обчислень...");
        long startTime = System.nanoTime();
        SearchTask task = new SearchTask(array, 0, rows);
        Integer result = pool.invoke(task);
        long endTime = System.nanoTime();

        System.out.println(result != null
                ? "Знайдено елемент: " + result
                : "Елемент не знайдено.");
        System.out.println("Час виконання: " + (endTime - startTime) / 1_000_000 + " мс");
    }

    static class SearchTask extends RecursiveTask<Integer> {
        private static final int THRESHOLD = 50;
        private final int[][] array;
        private final int startRow, endRow;

        public SearchTask(int[][] array, int startRow, int endRow) {
            this.array = array;
            this.startRow = startRow;
            this.endRow = endRow;
        }

        @Override
        protected Integer compute() {
            if (endRow - startRow <= THRESHOLD) {
                for (int i = startRow; i < endRow; i++) {
                    for (int j = 0; j < array[i].length; j++) {
                        System.out.println("Перевірка елементу: array[" + i + "][" + j + "] = " + array[i][j] + ", сума індексів = " + (i + j));
                        if (array[i][j] == i + j) {
                            return array[i][j];
                        }
                    }
                }
                return null;
            } else {
                int mid = (startRow + endRow) / 2;
                SearchTask task1 = new SearchTask(array, startRow, mid);
                SearchTask task2 = new SearchTask(array, mid, endRow);
                task1.fork();
                Integer result = task2.compute();
                return result != null ? result : task1.join();
            }
        }
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
        System.out.println("Згенерований масив:");
        for (int i = 0; i < array.length; i++) {
            System.out.print("Рядок " + (i + 1) + ": ");
            for (int value : array[i]) {
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
