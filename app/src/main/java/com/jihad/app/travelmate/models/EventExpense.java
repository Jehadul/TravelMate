package com.jihad.app.travelmate.models;

public class EventExpense extends BaseModel {

    private String uid;
    private double amount;
    private String expenseDate;
    private String type;
    private String description;

    public EventExpense() {
    }

    public EventExpense(String uid, double amount, String expenseDate, String type, String description) {
        super();
        this.uid = uid;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.type = type;
        this.description = description;
    }

    public EventExpense(String uid, double amount, String expenseDate, String type, String description, String createdDate, String createdTime) {
        super(createdDate, createdTime);
        this.uid = uid;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.type = type;
        this.description = description;
    }

    public String getUid() {
        return uid;
    }

    public double getAmount() {
        return amount;
    }

    public String getExpenseDate() {
        return expenseDate;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
