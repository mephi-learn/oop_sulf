package info.sitnikov.finance.entity.menu;

import info.sitnikov.finance.model.Amount;
import info.sitnikov.finance.model.Category;
import info.sitnikov.finance.model.Wallet;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class AmountEdit extends AbstractMenu {
    public AmountEdit() {
        super("Редактирование платежа");
    }

    @Override
    public void accept(@NotNull Context context) {
        // Получаем кошелёк
        Optional<Wallet> optionalWallet = context.walletSelected();

        // Если кошелёк не найден, то сообщаем, что данный пункт меню для него недоступен
        if (optionalWallet.isEmpty()) {
            context.errorln("Выберите кошелёк");
            return;
        }

        // Выбираем категорию
        Optional<Category> optionalCategory = context.service.selectCategoryMenu(context);
        if (optionalCategory.isEmpty()) {
            return;
        }

        // Выбираем платёж
        Optional<Amount> optionalAmount = context.service.selectAmountMenu(context, optionalCategory.get());
        if (optionalAmount.isEmpty()) {
            return;
        }

        Amount amount = optionalAmount.get();


        String description = context.selectStringDefault("Введите описание платежа", amount.getDescription());

        // Выбор расход или доход
        List<String> targets = Arrays.asList("Расход", "Доход");
        for (int i = 0; i < targets.size(); i++) {
            context.println("%4d: [%s]", i + 1, targets.get(i));
        }
        context.printLine();
        context.print("> ");

        String in = context.inputString();
        String target;
        try {
            int index = Integer.parseInt(in) - 1;
            if (index >= 0 && index < targets.size()) {
                target = targets.get(index);
            } else {
                context.errorln("Неверный номер. %s", in);
                return;
            }
        } catch (Exception ex) {
            context.errorln("Ошибка ввода. %s", in);
            return;
        }

        // Ввод суммы платежа и корректировка с учётом расхода/дохода
        Number amountSum = context.selectNumberDefault("Введите сумму платежа", amount.getAmount());
        amountSum = Math.abs(amountSum.doubleValue());
        if (target.equals("Расход")) {
            amountSum = amountSum.doubleValue() * -1;
        }

        // Ввод даты/времени платежа
        String date = context.selectStringDefault("Введите дату платежа в формате dd.mm.yyyy HH:mm:ss (время необязательно)",
                amount.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).trim();
        if (date.length() == 10) {
            date += " 00:00:00";
        }
        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        } catch (Exception ex) {
            context.errorln("Некорректный формат даты/времени: %s", date);
            return;
        }

        context.service.editAmount(amount, optionalCategory.get(), description, amountSum.doubleValue(), dateTime);

        // Сохраняем репозиторий
        context.service.storeRepository();
    }
}
