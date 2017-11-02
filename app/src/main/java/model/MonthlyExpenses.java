package model;

/**
 * Created by root on 19-10-17.
 */

public class MonthlyExpenses {
    public String month;
    private float income, expenses;

    public MonthlyExpenses(){
        //default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public MonthlyExpenses(String month, float income, float expenses) {
        this.month = month;
        this.income = income;
        this.expenses = expenses;
    }

    public String getMonth() {
        return month;
    }

    public float getExpenses() {
        return expenses;
    }

    public float getIncome() {
        return income;
    }

    public void setIncome(float income) {
        this.income = income;
    }

    public void setExpenses(float expenses) {
        this.expenses = expenses;
    }
}
