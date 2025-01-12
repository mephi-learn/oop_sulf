package info.sitnikov.finance.entity.menu;

import info.sitnikov.finance.entity.security.Authentication;
import info.sitnikov.finance.model.Wallet;

import java.util.Optional;

public final class UserList extends AbstractMenu {
    public UserList() {
        super("Список пользователей");
    }

    @Override
    public void accept(Context context) {
        context.service.selectUserMenu(context, true);
        context.println("");
    }
}
