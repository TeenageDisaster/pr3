import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Клас, що представляє клієнта
class Customer {
    private final int id;

    public Customer(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

// Склад як спільний ресурс
class Stock {
    private final Queue<Customer> queue = new LinkedList<>();
    private final Lock lock = new ReentrantLock();

    public void addCustomer(Customer customer) {
        lock.lock();
        try {
            queue.add(customer);
            System.out.println("Клієнт №" + customer.getId() + " доданий до черги.");
        } finally {
            lock.unlock();
        }
    }

    public Customer getNextCustomer() {
        lock.lock();
        try {
            return queue.poll();
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return queue.isEmpty();
        } finally {
            lock.unlock();
        }
    }
}

// Каса як потік
class CashRegister extends Thread {
    private final Stock stock;

    public CashRegister(Stock stock) {
        this.stock = stock;
    }

    @Override
    public void run() {
        while (true) {
            Customer customer;
            synchronized (stock) {
                customer = stock.getNextCustomer();
            }
            if (customer != null) {
                System.out.println("Каса починає обслуговувати клієнта №" + customer.getId());
                try {
                    Thread.sleep(new Random().nextInt(3000) + 1000);
                } catch (InterruptedException e) {
                    System.out.println("Обслуговування клієнта №" + customer.getId() + " було перервано.");
                }
                System.out.println("Каса завершила обслуговування клієнта №" + customer.getId());
            } else {
                try {
                    Thread.sleep(500); // Затримка, якщо черга порожня
                } catch (InterruptedException e) {
                    System.out.println("Робота каси перервана.");
                }
            }
        }
    }
}

// Головний клас
public class Supermarket {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Stock stock = new Stock();
        int customerId = 1;

        System.out.println("Введіть \"open\", щоб відкрити супермаркет.");
        while (!scanner.nextLine().equalsIgnoreCase("open")) {
            System.out.println("Супермаркет поки закритий. Введіть \"open\", щоб відкрити.");
        }

        System.out.println("Супермаркет відкрився!");

        // Створення потоків кас
        Thread cashRegister1 = new CashRegister(stock);
        Thread cashRegister2 = new CashRegister(stock);
        cashRegister1.start();
        cashRegister2.start();

        Thread inputListener = new Thread(() -> {
            while (true) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("close")) {
                    System.out.println("Супермаркет закривається...");
                    System.exit(0);
                }
            }
        });

        inputListener.setDaemon(true);
        inputListener.start();

        // Додавання клієнтів у чергу
        while (true) {
            Customer customer = new Customer(customerId++);
            stock.addCustomer(customer);

            try {
                Thread.sleep(1000); // Затримка між додаванням клієнтів
            } catch (InterruptedException e) {
                System.out.println("Помилка у симуляції затримки.");
            }
        }
    }
}
