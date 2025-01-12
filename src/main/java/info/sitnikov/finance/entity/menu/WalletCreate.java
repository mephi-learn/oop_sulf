package info.sitnikov.finance.entity.menu;

import info.sitnikov.finance.entity.security.Authentication;
import info.sitnikov.finance.model.User;
import info.sitnikov.finance.model.Wallet;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class WalletCreate extends AbstractMenu {
    public WalletCreate() {
        super("Создание кошелька");
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

        String walletName = context.selectString("Введите название для кошелька");
        String currency = context.selectStringDefault("Введите валюту кошелька", "RUB");


        // Создаём кошелёк
        Optional<Wallet> optionalWallet = context.service.createWalletInUser(sessionUser.get(), walletName, currency);
        if (optionalWallet.isEmpty()) {
            context.errorln("У вас уже имеется кошелёк с таким названием: %s", walletName);
            return;
        }

        // Автоматически переключаемся на созданный кошелёк
        context.authorized().ifPresent(session -> {
            session.setWallet(optionalWallet.get());

            // Автоматически создаём категорию по-умолчанию в этом кошельке, которую нельзя ни изменить, ни удалить
            optionalWallet.ifPresent(wallet -> context.service.createCategoryInWallet(wallet, "default", 0));
        });

        // Сохраняем репозиторий
        context.service.storeRepository();
    }
}
