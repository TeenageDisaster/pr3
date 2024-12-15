import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;

class FactorialCalculator {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(5); // Пул з 5 потоків
        List<Future<Long>> results = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        System.out.println("Введіть 'start', щоб почати обчислення випадкових факторіалів, або 'stop', щоб завершити програму.");

        while (!scanner.nextLine().equalsIgnoreCase("start")) {
            System.out.println("Будь ласка, введіть 'start', щоб почати.");
        }

        System.out.println("Обчислення почалося. Введіть 'stop', щоб завершити.");

        Thread inputListener = new Thread(() -> {
            while (true) {
                if (scanner.nextLine().equalsIgnoreCase("stop")) {
                    System.out.println("Завершення програми...");
                    executor.shutdownNow();
                    System.exit(0);
                }
            }
        });

        inputListener.setDaemon(true);
        inputListener.start();

        // Постійне обчислення факторіалів випадкових чисел
        while (true) {
            int number = random.nextInt(20) + 1; // Випадкове число від 1 до 20
            Callable<Long> task = () -> calculateFactorial(number);
            Future<Long> future = executor.submit(task);
            results.add(future);

            try {
                Long factorial = future.get(); // Очікуємо завершення задачі
                System.out.println("Факторіал числа " + number + " = " + factorial);
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Помилка при обчисленні факторіала для числа " + number);
                e.printStackTrace();
            }

            try {
                Thread.sleep(1000); // Затримка між обчисленнями
            } catch (InterruptedException e) {
                System.out.println("Обчислення було перервано.");
                break;
            }
        }
    }

    // Метод для обчислення факторіала
    public static long calculateFactorial(int number) {
        long result = 1;
        for (int i = 2; i <= number; i++) {
            result *= i;
        }
        return result;
    }
}
