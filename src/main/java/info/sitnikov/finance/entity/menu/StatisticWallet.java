package info.sitnikov.finance.entity.menu;

import info.sitnikov.finance.entity.security.Authentication;
import info.sitnikov.finance.model.Wallet;

import java.util.Optional;

public final class StatisticWallet extends AbstractMenu {
    public StatisticWallet() {
        super("Статистика по кошельку");
    }

    @Override
    public void accept(Context context) {
        // Получаем выбранный кошелёк
        Optional<Wallet> sessionWallet = context.authorized().map(Authentication.Session::wallet);

        // Если кошелёк не выбран, то сообщаем, что данный пункт меню недоступен
        if (sessionWallet.isEmpty()) {
            context.errorln("Необходимо выбрать кошелёк");
            return;
        }

        Wallet wallet = sessionWallet.get();
        context.println("");
        context.println("Кошелёк: %s", wallet.getName());
        context.println("Баланс: %.2f", wallet.incomes() - wallet.expenses());
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
