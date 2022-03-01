import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        long start = System.currentTimeMillis();

        Bank bank = new Bank();
        ArrayList<Thread> thread1 = new ArrayList<>();


        for (int i = 0; i < 1000; i++) {
            thread1.add(new Client(bank, i));
        }

        bank.getSumAllAccounts();

        for (int i = 0; i < thread1.size(); i++) {
            thread1.get(i).start();
        }

        for (int i = 0; i < thread1.size(); i++) {
            thread1.get(i).join();
        }

        bank.getSumAllAccounts();
        System.out.println("Общее время работы - " + ((System.currentTimeMillis() - start) / 1000) + " сек.");
    }
}
