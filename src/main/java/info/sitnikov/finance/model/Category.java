package info.sitnikov.finance.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Data
public class Category implements AmountOperations, Serializable {
    final String id;
    String name;
    double budget;
    final String walletId;
    final Map<String, Amount> amounts;

    public Category(String walletId, String name, double budget) {
        this.id = UUID.randomUUID().toString();
        this.walletId = walletId;
        this.name = name;
        this.budget = Math.abs(budget);
        this.amounts = new HashMap<>();
    }

    public boolean isEmpty() {
        return this.amounts.isEmpty();
    }

    public void addAmount(Amount amount) {
        this.amounts.put(amount.getId(), amount);
    }

    public Optional<Amount> getAmountById(String id) {
        for (Amount amount : this.amounts.values()) {
            if (amount.getId().equals(id)) {
                return Optional.of(amount);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean removeAmountById(String amountId) {
        return this.amounts.values().removeIf(entry -> entry.getId().equals(amountId));
    }

    @Override
    public boolean removeAmountByTime(LocalDateTime time) {
        return this.amounts.values().removeIf(entry -> entry.getDate().isEqual(time));
    }

    @Override
    public boolean removeAmounts() {
        return removeAmounts(LocalDateTime.MIN, LocalDateTime.MAX);
    }

    @Override
    public boolean removeAmounts(LocalDateTime from) {
        return removeAmounts(from, LocalDateTime.MAX);
    }

    @Override
    public boolean removeAmounts(LocalDateTime from, LocalDateTime to) {
        return this.amounts.values().removeIf(entry -> (entry.getDate().isAfter(from) && entry.getDate().isBefore(to))
                || entry.getDate().isEqual(from) || entry.getDate().isEqual(to));
    }

    @Override
    public List<Amount> filterAmounts() {
        return filterAmounts(LocalDateTime.MIN, LocalDateTime.MAX);
    }

    @Override
    public List<Amount> filterAmounts(LocalDateTime from) {
        return filterAmounts(from, LocalDateTime.MAX);
    }

    @Override
    public List<Amount> filterAmounts(LocalDateTime from, LocalDateTime to) {
        return new ArrayList<>(this.amounts.values());
//        return this.amounts.values().stream().filter(amount -> (amount.getDate().isAfter(from) && amount.getDate().isBefore(to))
//                || amount.getDate().isEqual(from) || amount.getDate().isEqual(to)).sorted(Comparator.comparing(Amount::getDate)).toList();
    }

    @Override
    public double calculate() {
        return calculate(LocalDateTime.MIN, LocalDateTime.MAX);
    }

    @Override
    public double calculate(LocalDateTime from) {
        return calculate(from, LocalDateTime.MAX);
    }

    @Override
    public double calculate(LocalDateTime from, LocalDateTime to) {
        return this.amounts.values().stream().filter(amount -> (amount.getDate().isAfter(from) && amount.getDate().isBefore(to))
                || amount.getDate().isEqual(from) || amount.getDate().isEqual(to)).mapToDouble(Amount::getAmount).sum();
    }

    @Override
    public double expenses() {
        return expenses(LocalDateTime.MIN, LocalDateTime.MAX);
    }

    @Override
    public double expenses(LocalDateTime from) {
        return expenses(from, LocalDateTime.MAX);
    }

    @Override
    public double expenses(LocalDateTime from, LocalDateTime to) {
        return this.amounts.values().stream().filter(amount -> amount.getAmount() < 0 && ((amount.getDate().isAfter(from) && amount.getDate().isBefore(to))
                || amount.getDate().isEqual(from) || amount.getDate().isEqual(to))).mapToDouble(Amount::getAmount).sum();
    }

    @Override
    public double incomes() {
        return incomes(LocalDateTime.MIN, LocalDateTime.MAX);
    }

    @Override
    public double incomes(LocalDateTime from) {
        return incomes(from, LocalDateTime.MAX);
    }

    @Override
    public double incomes(LocalDateTime from, LocalDateTime to) {
        return this.amounts.values().stream().filter(amount -> amount.getAmount() > 0 && ((amount.getDate().isAfter(from) && amount.getDate().isBefore(to))
                || amount.getDate().isEqual(from) || amount.getDate().isEqual(to))).mapToDouble(Amount::getAmount).sum();
    }
}
