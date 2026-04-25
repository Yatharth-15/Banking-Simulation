package model;

public class Loan {
    private int loanId;
    private int accountNo;
    private String loanType;
    private double principal;
    private double interestRate;
    private int months;
    private double emi;
    private String status;

    public Loan(int loanId, int accountNo, String loanType, double principal, double interestRate, int months, double emi, String status) {
        this.loanId = loanId;
        this.accountNo = accountNo;
        this.loanType = loanType;
        this.principal = principal;
        this.interestRate = interestRate;
        this.months = months;
        this.emi = emi;
        this.status = status;
    }

    // Getters
    public int getLoanId() { return loanId; }
    public int getAccountNo() { return accountNo; }
    public String getLoanType() { return loanType; }
    public double getPrincipal() { return principal; }
    public double getInterestRate() { return interestRate; }
    public int getMonths() { return months; }
    public double getEmi() { return emi; }
    public String getStatus() { return status; }

    // Setters
    public void setStatus(String status) { this.status = status; }
    public void setLoanId(int loanId) { this.loanId = loanId; }
}
