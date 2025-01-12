package info.sitnikov.finance.entity.menu;

import info.sitnikov.finance.entity.security.Authentication;
import info.sitnikov.finance.model.Amount;
import info.sitnikov.finance.model.Category;
import info.sitnikov.finance.model.User;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Optional;


public final class TransferMenu extends AbstractMenu {
    public TransferMenu() {
        super("Создание перевода");
    }

    @Override
    public void accept(@NotNull Context context) {
        // Получаем аутентифицированного пользователя
        Optional<User> sessionUser = context.authorized().map(Authentication.Session::user);

        // Если пользователь не аутентифицирован, то сообщаем, что данный пункт меню для него недоступен
        if (sessionUser.isEmpty()) {
            context.errorln("Требуется аутентификация");
            return;
        }

        // Выбираем пользователя
        Optional<User> optionalUser = context.service.selectUserMenu(context, false);
        if (optionalUser.isEmpty()) {
            return;
        }

        // Ввод суммы платежа и корректировка с учётом расхода/дохода
        Number amountSum = context.selectNumber("Введите сумму платежа");
        amountSum = Math.abs(amountSum.doubleValue());

        // Выбираем категорию, откуда будем списывать средства
        Optional<Category> optionalSourceCategory = context.service.selectCategoryMenu(context, false);
        if (optionalSourceCategory.isEmpty()) {
            return;
        }
        Category sourceCategory = optionalSourceCategory.get();

        // Определяем категорию, куда будем записывать средства
        Optional<Category> optionalDestinationCategory = optionalUser.get().defaultWalletCategory();
        if (optionalDestinationCategory.isEmpty()) {
            context.errorln("Не найден каталог для зачисления средств");
            return;
        }
        Category destinationCategory = optionalDestinationCategory.get();

        LocalDateTime date = LocalDateTime.now();

        // Списываем средства
        Optional<Amount> sourceAmount = context.service.createAmountInCategory(sourceCategory,
                "Отправка средств пользователю " + optionalUser.get().getUsername(),
                amountSum.doubleValue() * -1, date);
        if (sourceAmount.isEmpty()) {
            context.errorln("Проблемы добавления исходящего платежа");
            return;
        }

        // Зачисляем средства
        Optional<Amount> destinationAmount = context.service.createAmountInCategory(destinationCategory,
                "Приём средств от пользователя " + sessionUser.get().getUsername(),
                amountSum.doubleValue(), date);
        if (destinationAmount.isEmpty()) {
            context.errorln("Проблемы добавления приходящего платежа");

            // Если произошла ошибка зачисления, то удаляем ранее списанные средства
            sourceCategory.removeAmountById(sourceAmount.get().getId());
            return;
        }

        // Сохраняем репозиторий
        context.service.storeRepository();

        context.println("ПЕРЕВОД ВЫПОЛНЕН УСПЕШНО");
        context.println("");
    }
}
