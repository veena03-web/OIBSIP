import java.util.ArrayList;
import java.util.Scanner;

public class ATM{
    private String userId;
    private String userPin;
    private double balance;
    private ArrayList<String> transactionHistory;

    public ATM(String userId,String userPin, double balance){
        this.userId = userId;
        this.userPin = userPin;
        this.balance = balance;
        this.transactionHistory = new ArrayList<>();
    }

    public boolean login(String enteredId, String enteredPin){
        return enteredId.equals(userId) && enteredPin.equals(userPin);
    }

    public void showMenu(){
        Scanner sc = new Scanner(System.in);
        int choice;
        do{
            System.out.println("\n -------ATM Menu------");
            System.out.println("1. Transaction History");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Quit");
            System.out.println("Enter Your Choice: ");
            choice = sc.nextInt();

            switch(choice){
                case 1:
                    showTransactionHistory();
                    break;
                case 2:
                    withdraw(sc);
                    break;
                case 3:
                    deposit(sc);
                    break;
                case 4:
                    transfer(sc);
                    break;
                case 5:
                    System.out.println("Thank You for using the ATM. GoodBye!");
                    break;
            }
        }while(choice != 5);
    }

    private void showTransactionHistory(){
        System.out.println("\n----Transaction History---");
        if(transactionHistory.isEmpty()){
            System.out.println("No Transaction yet.");
        }else{
            for(String transaction : transactionHistory){
                System.out.println(transaction);
            }
        }
    }

    private void withdraw(Scanner sc){
        System.out.println("Enter amount to withdraw: ");
        double amount = sc.nextDouble();
        if(amount > 0 && amount <= balance){
            balance -= amount;
            transactionHistory.add("Withdraw: $"+ amount);
            System.out.println("Withdraw successful. Remaining balance: $" + balance);
        }else{
            System.out.println("Invalid amount or Insufficient balance.");
        }
    }

    private void deposit(Scanner sc){
        System.out.print("Enter amount to deposit: ");
        double amount = sc.nextDouble();
        if(amount > 0){
            balance += amount;
            transactionHistory.add("Deposit: $"+amount);
            System.out.println("Deposit successful. Current balance: $" + balance);

        }else{
            System.out.println("Invalid amount");
        }
    }

    private void transfer(Scanner sc){
        System.out.print("Enter recipient's User ID: ");
        String recipientId = sc.next();
        System.out.print("Enter amount to transfer: ");
        double amount = sc.nextDouble();
        if(amount > 0 && amount <= balance){
            balance -= amount;
            transactionHistory.add("Transfer to " + recipientId + ": $" + amount);
            System.out.println("Transfer Successful. Remaining balance: $"+balance);
        }else{
            System.out.println("Invalid amount or insufficient balance.");
        }
    }

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);

        System.out.print("Set your User ID: ");
        String id = sc.next();
        System.out.print("Set your PIN: ");
        String pin = sc.next();
        System.out.print("Enter your initial balance: $");
        double balance = sc.nextDouble();

        ATM atm = new ATM(id,pin,balance);

        System.out.print("\n Enter User ID: ");
        String enteredId = sc.next();
        System.out.print("Enter PIN: ");
        String enteredPin = sc.next();

        if(atm.login(enteredId, enteredPin)){
            atm.showMenu();
        }else{
            System.out.println("Invalid User ID or PIN. Access Denied.");
        }
    }
}