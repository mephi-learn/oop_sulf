package info.sitnikov.finance.entity.menu;

import info.sitnikov.finance.entity.security.Authentication;
import info.sitnikov.finance.model.User;
import info.sitnikov.finance.model.Wallet;

import java.util.Optional;

public final class WalletList extends AbstractMenu {
    public WalletList() {
        super("Список кошельков");
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

        context.service.selectWalletMenu(context, true);
        context.println("");
    }
}
