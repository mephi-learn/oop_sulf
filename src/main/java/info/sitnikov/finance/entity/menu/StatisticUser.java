package info.sitnikov.finance.entity.menu;

import info.sitnikov.finance.entity.security.Authentication;
import info.sitnikov.finance.model.User;

import java.util.Optional;

public final class StatisticUser extends AbstractMenu {
    public StatisticUser() {
        super("Статистика по пользователю");
    }

    @Override
    public void accept(Context context) {
        // Получаем аутентифицированного пользователя
        Optional<User> sessionUser = context.authorized().map(Authentication.Session::user);

        // Если пользователь не аутентифицирован, то сообщаем, что данный пункт меню для него недоступен
        if (sessionUser.isEmpty()) {
            context.errorln("Требуется аутентификация");
            return;
        }

        User user = sessionUser.get();

        context.println("");
        context.printLine();
        context.println("СТАТИСТИКА ПО ПОЛЬЗОВАТЕЛЮ");
        context.printLine();

        context.println("");
        context.println("Пользователь: %s", user.getUsername());
        context.println("Баланс: %.2f", user.incomes() + user.expenses());
        context.printLine();
        context.println("Общий доход: %.2f", user.incomes());
        context.println("Доходы по кошелькам:");
        user.getWallets().values().stream()
                .filter(wallet -> wallet.incomes() > 0)
                .forEach(wallet -> {
                    context.println("\t%s: %.2f", wallet.getName(), wallet.incomes());
                });
        context.println("");
        context.println("Общий расход: %.2f", Math.abs(user.expenses()));
        context.println("Расходы по кошелькам:");
        user.getWallets().values().stream()
                .filter(wallet -> wallet.expenses() < 0)
                .forEach(wallet -> {
                    context.println("\t%s: %.2f", wallet.getName(), Math.abs(wallet.expenses()));
                });
        context.println("");
        context.println("Общий баланс: %.2f", user.incomes() + user.expenses());
        context.println("Баланс по кошелькам:");
        user.getWallets().values().stream()
                .filter(wallet -> wallet.incomes() + wallet.expenses() != 0)
                .forEach(wallet -> {
                    context.println("\t%s: %.2f", wallet.getName(), wallet.incomes() + wallet.expenses());
                });
        context.println("");
        context.println("Бюджеты по кошелькам:");
        user.getWallets().values().stream()
                .filter(wallet -> wallet.getBudget() != 0)
                .forEach(wallet -> {
                    double budget = wallet.getBudget() - wallet.expenses();
                    if (budget != 0) {
                        budget *= -1;
                    }
                    context.println("\t%s: %.2f, Оставшийся бюджет: %.2f", wallet.getName(), Math.abs(wallet.getBudget()), budget);
                });
        context.println("");
        context.printLine();
        context.println("СТАТИСТИКА ПО КОШЕЛЬКАМ");
        context.printLine();


        for (var wallet : user.getWallets().values()) {
            context.println("");
            context.println("Кошелёк: %s", wallet.getName());
            context.println("Баланс: %.2f", wallet.incomes() + wallet.expenses());
            context.printLine();
            context.println("Общий доход: %.2f", wallet.incomes());
            context.println("Доходы по категориям:");
            wallet.getCategories().values().stream()
                    .filter(category -> category.incomes() > 0)
                    .forEach(category -> {
                        context.println("\t%s: %.2f", category.getName(), category.incomes());
                    });
            context.println("");
            context.println("Общий расход: %.2f", Math.abs(wallet.expenses()));
            context.println("Расходы по категориям:");
            wallet.getCategories().values().stream()
                    .filter(category -> category.expenses() < 0)
                    .forEach(category -> {
                        context.println("\t%s: %.2f", category.getName(), Math.abs(category.expenses()));
                    });
            context.println("");
            context.println("Общий баланс: %.2f", wallet.incomes() + wallet.expenses());
            context.println("Баланс по категориям:");
            wallet.getCategories().values().stream()
                    .filter(category -> category.incomes() + category.expenses() != 0)
                    .forEach(category -> {
                        context.println("\t%s: %.2f", category.getName(), category.incomes() + category.expenses());
                    });
            context.println("");
            context.println("Бюджеты по категориям:");
            wallet.getCategories().values().stream()
                    .filter(category -> category.getBudget() != 0)
                    .forEach(category -> {
                        double budget = category.getBudget() - category.expenses();
                        if (budget != 0) {
                            budget *= -1;
                        }
                        context.println("\t%s: %.2f, Оставшийся бюджет: %.2f", category.getName(), Math.abs(category.getBudget()), budget);
                    });

            context.println("");
        }
    }
}
