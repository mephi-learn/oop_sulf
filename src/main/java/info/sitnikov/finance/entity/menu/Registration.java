package info.sitnikov.finance.entity.menu;

import info.sitnikov.finance.entity.security.Authentication;
import info.sitnikov.finance.model.User;
import info.sitnikov.finance.model.Wallet;
import info.sitnikov.finance.repository.Repository;

import java.util.Optional;

public final class Registration extends AbstractMenu {
    private final Repository repository;

    public Registration(Repository repository) {
        super("Регистрация");
        this.repository = repository;
    }

    @Override
    public void accept(Context context) {
        String login = context.selectString("Введите логин");
        Optional<User> search = repository.findUserByUserName(login);
        if (search.isPresent()) {
            context.errorln("Пользователь с именем '%s' уже существует", login);
            return;
        }
        String password = context.selectString("Введите пароль");
        String confirm = context.selectString("Подтвердите пароль");
        if (!password.equals(confirm)) {
            context.errorln("Пароль не совпадает");
            return;
        }
        context.clearSession();
        User user = context.service.createUser(login, password);
        context.putSession(Authentication.Session.of(user));

        // Автоматически создаём кошелёк по-умолчанию, который нельзя ни изменить, ни удалить
        Optional<Wallet> optionalWallet = context.service.createWalletInUser(user, "default", "RUB");

        // Автоматически создаём категорию по-умолчанию в этом кошельке, которую нельзя ни изменить, ни удалить, и переключаемся на этот кошелёк
        optionalWallet.ifPresent(wallet -> {
            context.service.createCategoryInWallet(wallet, "default", 0);
            context.authorized().ifPresent(session -> {
                session.setWallet(optionalWallet.get());
            });
        });

        repository.store();
    }
}
