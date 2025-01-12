package info.sitnikov.finance.model;

import com.google.gson.annotations.Expose;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Data
public class Wallet implements AmountOperations, Serializable {
    final String id;
    String name;
    String currency;
    LocalDateTime time;
    final String userId;
    @Expose
    final Map<String, Category> categories;

    public Wallet(String userId, String walletName, String currency) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.name = walletName;
        this.currency = currency;
        this.time = LocalDateTime.now();
        this.categories = new HashMap<>();
    }

    public boolean isEmpty() {
        return this.categories.isEmpty();
    }

    public boolean addCategory(Category category) {
        // Если в кошельке уже есть категория с таким же названием, выходим с неудачей
        if (this.getCategoryByName(category.getName()).isPresent()) {
            return false;
        }

        // Добавляем категорию
        this.categories.put(category.getName(), category);
        return true;
    }

    public Optional<Category> getCategoryByName(String name) {
        return Optional.ofNullable(this.categories.get(name));
    }

    public boolean removeCategoryByName(String categoryName) {
        return this.categories.values().removeIf(entry -> entry.getName().equals(name));
    }

    public boolean renameCategory(String oldCategoryName, String newCategoryName) {

        // Если не существует переименовываемой категории или присутствует категория, в которую необходимо переименовать - возвращаем неудачу
        Optional<Category> category = this.getCategoryByName(oldCategoryName);
        if (category.isEmpty() || this.getCategoryByName(newCategoryName).isPresent()) {
            return false;
        }
        ;

        // Переименовываем категорию
        category.get().setName(newCategoryName);
        return true;
    }

    public double getBudget() {
        return this.categories.values().stream().map(Category::getBudget).filter(budget -> budget != 0).reduce(0.0, Double::sum);
    }

    @Override
    public boolean removeAmountById(String amountId) {
        for (Category category : this.categories.values()) {
            if (category.removeAmountById(amountId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeAmountByTime(LocalDateTime time) {
        boolean success = false;
        for (Category category : this.categories.values()) {
            if (category.removeAmountByTime(time)) {
                success = true;
            }
        }
        return success;
    }

    @Override
    public boolean removeAmounts() {
        return this.removeAmounts(LocalDateTime.MIN, LocalDateTime.MAX);
    }

    @Override
    public boolean removeAmounts(LocalDateTime from) {
        return this.removeAmounts(from, LocalDateTime.MAX);
    }

    @Override
    public boolean removeAmounts(LocalDateTime from, LocalDateTime to) {
        boolean success = false;
        for (Category category : this.categories.values()) {
            if (category.removeAmounts(from, to)) {
                success = true;
            }
        }
        return success;
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
        return this.categories.values().stream().flatMap(category -> filterAmounts(from, to).stream()).sorted(Comparator.comparing(Amount::getDate)).toList();
    }

    @Override
    public double calculate() {
        return this.calculate(LocalDateTime.MIN, LocalDateTime.MAX);
    }

    @Override
    public double calculate(LocalDateTime from) {
        return this.calculate(from, LocalDateTime.MAX);
    }

    @Override
    public double calculate(LocalDateTime from, LocalDateTime to) {
        return this.categories.values().stream().mapToDouble(category -> category.calculate(from, to)).sum();
    }

    @Override
    public double expenses() {
        return this.expenses(LocalDateTime.MIN, LocalDateTime.MAX);
    }

    @Override
    public double expenses(LocalDateTime from) {
        return this.expenses(from, LocalDateTime.MAX);
    }

    @Override
    public double expenses(LocalDateTime from, LocalDateTime to) {
        return this.categories.values().stream().mapToDouble(category -> category.expenses(from, to)).sum();
    }

    @Override
    public double incomes() {
        return this.incomes(LocalDateTime.MIN, LocalDateTime.MAX);
    }

    @Override
    public double incomes(LocalDateTime from) {
        return this.incomes(from, LocalDateTime.MAX);
    }

    @Override
    public double incomes(LocalDateTime from, LocalDateTime to) {
        return this.categories.values().stream().mapToDouble(category -> category.incomes(from, to)).sum();
    }
}
