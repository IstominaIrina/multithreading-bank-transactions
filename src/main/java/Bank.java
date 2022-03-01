import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Bank {

    private Map<String, Account> accounts;
    private final Random random = new Random();

    private static final Object tieLock = new Object();

    public Bank() {
        accounts = new ConcurrentHashMap<String, Account>();
    }

    public synchronized String registerAcc(long money) {
        Account account = new Account();
        String part1 = "408178100999";
        int part2 = 10000000 + accounts.size();
        String accNumber = part1 + part2;
        account.setAccNumber(accNumber);
        account.setMoney(money);
        accounts.put(accNumber, account);
        return accNumber;
    }

    public String getRandomAccount(String number, long amount, int name) {
        Random generator = new Random();
        Object[] keys = accounts.keySet().toArray();
        int i = 0;
        while (true) {
            String keysAccount = (String) keys[generator.nextInt(keys.length)];
            if (keysAccount.equals(number)) {
                continue;
            }
            return keysAccount;
        }
    }

    public synchronized boolean isFraud(String fromAccountNum, String toAccountNum, long amount)
            throws InterruptedException {
        Thread.sleep(1000);
        return random.nextBoolean();
    }

    public void transferMoney(Account fromAcc, Account toAcc, long amount) throws InterruptedException {

        if (fromAcc.getAccNumber().equals(toAcc.getAccNumber())) {
            System.out.println("Проверьте правильность счетов.");
            return;
        }

        if (fromAcc.getStatus() == false || toAcc.getStatus() == false) {
            System.out.println("! Денежные средства не могут быть переведены, один из счетов заблокирован (" + fromAcc.getAccNumber() + " / " + toAcc.getAccNumber() + ").");
            return;
        }

        if (amount > fromAcc.getMoney()) {
            System.out.println("- На вашем счете " + fromAcc.getAccNumber() + ", для перевода суммы " + amount + " недостаточно денежных средств - " + fromAcc.getMoney());
            return;
        }
        if (amount > 50000) {
            isFraud(fromAcc.getAccNumber(), toAcc.getAccNumber(), amount);
            if (isFraud(fromAcc.getAccNumber(), toAcc.getAccNumber(), amount) == true) {
                fromAcc.setStatus(false);
                toAcc.setStatus(false);
                System.out.println("! Денежный перевод на сумму " + amount + ", не безопасен, счета " + fromAcc.getAccNumber() + ", " + toAcc.getAccNumber() + " заблокированы!");
                return;
            } else {
                long fromSum = fromAcc.getMoney() - amount;
                fromAcc.setMoney(fromSum);

                long toSum = toAcc.getMoney() + amount;
                toAcc.setMoney(toSum);

                System.out.println("Со счета " + fromAcc.getAccNumber() + " перевод суммы " + amount + " одобрен, остаток " + fromSum + ", на счет " + toAcc.getAccNumber() + ", средства поступили, остаток " + toSum);
            }
        } else {
            long fromSum = fromAcc.getMoney() - amount;
            fromAcc.setMoney(fromSum);

            long toSum = toAcc.getMoney() + amount;
            toAcc.setMoney(toSum);

            System.out.println("Со счета " + fromAcc.getAccNumber() + " перевод суммы " + amount + " одобрен, остаток " + fromSum + ", на счет " + toAcc.getAccNumber() + ", средства поступили, остаток " + toSum);
        }
    }

    public void transfer(String fromAccountNum, String toAccountNum, long amount) throws InterruptedException {
        Account fromAcc = accounts.get(fromAccountNum);
        Account toAcc = accounts.get(toAccountNum);

        int fromHash = System.identityHashCode(fromAcc);
        int toHash = System.identityHashCode(toAcc);

        if (fromHash < toHash) {
            synchronized (fromAcc) {
                synchronized (toAcc) {
                    transferMoney(fromAcc, toAcc, amount);
                }
            }
        } else if (fromHash > toHash) {
            synchronized (toAcc) {
                synchronized (fromAcc) {
                    transferMoney(fromAcc, toAcc, amount);
                }
            }
        } else {
            synchronized (tieLock) {
                synchronized (fromAcc) {
                    synchronized (toAcc) {
                        transferMoney(fromAcc, toAcc, amount);
                    }
                }
            }
        }
    }

    public long getBalance(String accountNum) {
        Account accountNumber = accounts.get(accountNum);
        return accountNumber.getMoney();
    }

    public long getSumAllAccounts() {
        long sum = 0;
        for (Map.Entry<String, Account> entry : accounts.entrySet()) {
            Account value = entry.getValue();
            sum = sum + value.getMoney();
        }
        System.out.println("\nСумма общих денежных средств в банке составляет - " + sum + " рублей.\n");
        return sum;
    }
}
