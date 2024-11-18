import java.util.*;
import java.util.concurrent.*;

public class ParallelProduct {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Scanner scanner = new Scanner(System.in);

        // Запит діапазону чисел у користувача
        System.out.print("Введіть мінімальне значення діапазону (мінімум 0): ");
        int minRange = Math.max(scanner.nextInt(), 0);
        System.out.print("Введіть максимальне значення діапазону (максимум 100): ");
        int maxRange = Math.min(scanner.nextInt(), 100);

        // Випадковий розмір масиву від 40 до 60
        Random rand = new Random();
        int size = 40 + rand.nextInt(21);

        int[] numbers = new int[size];

        // Заповнення масиву випадковими числами в заданому діапазоні
        for (int i = 0; i < size; i++) {
            numbers[i] = rand.nextInt(maxRange - minRange + 1) + minRange;
        }

        System.out.println("Input Array: " + Arrays.toString(numbers));
        System.out.println("Length of the input Array: " + numbers.length);

        // Встановлення кількості потоків
        int numThreads = 9;
        int chunkSize = (int) Math.ceil((double) size / numThreads);

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        List<Future<List<Integer>>> futures = new ArrayList<>();
        CopyOnWriteArraySet<Integer> finalResult = new CopyOnWriteArraySet<>();

        long startTime = System.nanoTime();

        // Розподіл завдань між потоками
        for (int i = 0; i < numThreads; i++) {
            final int start = i * chunkSize;
            final int end = Math.min(start + chunkSize, size);

            Callable<List<Integer>> callable = () -> {
                List<Integer> resultList = new ArrayList<>();
                for (int j = start; j < end - 1; j += 2) {
                    resultList.add(numbers[j] * numbers[j + 1]);
                }
                return resultList;
            };

            futures.add(executorService.submit(callable));
        }

        for (Future<List<Integer>> future : futures) {
            try {
                if (future.isDone() && !future.isCancelled()) {
                    finalResult.addAll(future.get());
                }
            } catch (CancellationException e) {
                System.out.println("Один із завдань був скасований");
            }
        }

        System.out.println("Result (even-odd products): " + finalResult);
        System.out.println("Length of the result: " + finalResult.size());

        long endTime = System.nanoTime();
        System.out.println("Execution time (in ms): " + (endTime - startTime) / 1_000_000);

        executorService.shutdown();
    }
}
