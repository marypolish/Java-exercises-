import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class ParallelProduct {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Random rand = new Random();
        int size = 20;
        int[] numbers = new int[size];
        for (int i = 0; i < size; i++) {
            numbers[i] = rand.nextInt(101);
        }

        System.out.println("Input Array: " + Arrays.toString(numbers));

        // Розбиття масиву на частини для кожного потоку
        int numThreads = 5;
        int chunkSize = numbers.length / numThreads;
        int remainder = numbers.length % numThreads;

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        List<Future<List<Integer>>> futures = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            final int start = i * chunkSize + Math.min(i, remainder);
            final int end = Math.min(start + chunkSize + (i < remainder ? 1 : 0), numbers.length);

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
            finalResult.addAll(future.get());
        }

        System.out.println("Result (even-odd products): " + finalResult);

        System.out.println("Length of the result (using manual counting): " + finalResult.size());

        executorService.shutdown();
    }
}
