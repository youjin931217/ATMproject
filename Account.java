package main;

public class Account {
    private String accountId;
    private int pw;
    private String name;
    private long balance=0;
    
    public Account(String accountId, int pw, String name, long balance) {
        this.accountId = accountId;
        this.pw = pw;
        this.name = name;
        this.balance = balance;
    }
 
    public String getAccountId() {
        return accountId;
    }
 
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
 
    public int getPw() {
        return pw;
    }
 
    public void setPw(int pw) {
        this.pw = pw;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public long getBalance() {
        return balance;
    }
 
    public void setBalance(long balance) {
        this.balance = balance;
    }
}
