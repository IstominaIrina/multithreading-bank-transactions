public class Client extends Thread {

    private Bank bank;
    private int name;
    private String account;
    private String toAccount;
    private long money = (long) (65000 + (Math.random() * 10000));

    public Client(Bank _bank, int _name) {
        this.bank = _bank;
        this.name = _name;
        this.account = this.bank.registerAcc(money);
    }

    @Override
    public void run() {

        System.out.println("Зарегистрированный счет " + account + ", сумма -  " + money + " рублей.");

        for (int i = 0; i < 5; i++) {
            long amount = (long) (43000 + (Math.random() * 10000));
            this.toAccount = this.bank.getRandomAccount(this.account, amount, this.name);
            try {
                this.bank.transfer(this.account, this.toAccount, amount);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long balance = bank.getBalance(this.account);
        System.out.println("\nНа вашем счете " + this.account + ", " + balance + " рублей\n");
    }
}
