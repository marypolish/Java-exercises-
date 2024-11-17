import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.awt.Desktop;

public class ImageFinderStealing {
    private static final ForkJoinPool pool = new ForkJoinPool();
    private static final Pattern IMAGE_PATTERN = Pattern.compile(".*\\.(jpg|jpeg|png|gif)$", Pattern.CASE_INSENSITIVE);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введіть шлях до директорії: ");
        String dirPath = scanner.nextLine();

        File dir = new File(dirPath);
        if (!dir.isDirectory()) {
            System.out.println("Помилка: вказаний шлях не є директорією.");
            return;
        }

        System.out.println("Пошук файлів...");
        long startTime = System.nanoTime();
        ImageSearchTask task = new ImageSearchTask(dir);
        List<File> imageFiles = pool.invoke(task);
        long endTime = System.nanoTime();

        System.out.println("Кількість зображень: " + imageFiles.size());
        System.out.println("Час виконання: " + (endTime - startTime) / 1_000_000 + " мс");

        // Якщо зображення знайдено, відкриваємо останнє
        if (!imageFiles.isEmpty()) {
            // Сортуємо за датою останньої модифікації (останній модифікований файл)
            imageFiles.sort((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

            File lastImage = imageFiles.get(0); // Найновіший файл
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(lastImage);
                } else {
                    System.out.println("Не вдалося відкрити файл: Desktop не підтримується.");
                }
            } catch (Exception e) {
                System.out.println("Помилка при відкритті файлу: " + e.getMessage());
            }
        } else {
            System.out.println("Зображення не знайдено.");
        }
    }

    static class ImageSearchTask extends RecursiveTask<List<File>> {
        private final File dir;

        public ImageSearchTask(File dir) {
            this.dir = dir;
        }

        @Override
        protected List<File> compute() {
            File[] files = dir.listFiles();
            List<File> imageFiles = new ArrayList<>();

            if (files != null) {
                List<ImageSearchTask> subtasks = new ArrayList<>();

                for (File file : files) {
                    if (file.isDirectory()) {
                        ImageSearchTask subtask = new ImageSearchTask(file);
                        subtask.fork();
                        subtasks.add(subtask);
                    } else if (file.isFile() && IMAGE_PATTERN.matcher(file.getName()).matches()) {
                        imageFiles.add(file);
                    }
                }

                for (ImageSearchTask subtask : subtasks) {
                    imageFiles.addAll(subtask.join());
                }
            }

            return imageFiles;
        }
    }
}
