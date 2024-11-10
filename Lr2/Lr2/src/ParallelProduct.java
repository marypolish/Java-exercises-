import java.util.*;
import java.util.concurrent.*;

public class ParallelProduct {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Scanner scanner = new Scanner(System.in);

        // Запит діапазону чисел у користувача
        System.out.print("Введіть мінімальне значення діапазону: ");
        int minRange = scanner.nextInt();
        System.out.print("Введіть максимальне значення діапазону: ");
        int maxRange = scanner.nextInt();

        // Фіксований розмір масиву
        int size = 50;

        int[] numbers = new int[size];
        Random rand = new Random();

        // Заповнення масиву випадковими числами в заданому діапазоні
        for (int i = 0; i < size; i++) {
            numbers[i] = rand.nextInt(maxRange - minRange + 1) + minRange;
        }

        System.out.println("Input Array: " + Arrays.toString(numbers));
        System.out.println("Length of the input Array: " + numbers.length);

        // Встановлення кількості потоків
        int numThreads = 8;


        int chunkSize = size / numThreads;

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        List<Future<List<Integer>>> futures = new ArrayList<>();


        long startTime = System.nanoTime();


        for (int i = 0; i < numThreads; i++) {
            final int start = i * chunkSize;
            final int end = (i == numThreads - 1) ? size : (i + 1) * chunkSize;

            Callable<List<Integer>> callable = () -> {
                List<Integer> resultList = new ArrayList<>();
                for (int j = start; j < end - 1; j += 2) {
                    resultList.add(numbers[j] * numbers[j + 1]);
                }
                return resultList;
            };

            futures.add(executorService.submit(callable));
        }

        List<Integer> finalResult = new ArrayList<>();

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
