import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

class PrimeNumberChecker {
    public static void main(String[] args) {
        int[] numbers = new int[100];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = i + 1; // Масив чисел від 1 до 100
        }

        ExecutorService executor = Executors.newFixedThreadPool(10); // Пул з 10 потоків
        ConcurrentHashMap<Integer, Boolean> results = new ConcurrentHashMap<>();

        List<Future<?>> tasks = new ArrayList<>();

        for (int number : numbers) {
            Runnable task = () -> results.put(number, isPrime(number));
            tasks.add(executor.submit(task));
        }

        // Очікуємо завершення всіх задач
        for (Future<?> task : tasks) {
            try {
                task.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();

        // Вивід результатів
        results.forEach((number, isPrime) -> {
            System.out.println("Число " + number + (isPrime ? " є простим." : " не є простим."));
        });
    }

    // Метод для перевірки на простоту числа
    public static boolean isPrime(int number) {
        if (number <= 1) return false;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }
}
