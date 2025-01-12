package info.sitnikov.finance.entity.menu;

import info.sitnikov.finance.entity.security.Authentication;
import info.sitnikov.finance.model.User;
import info.sitnikov.finance.model.Wallet;

import java.util.Optional;

public final class CategoryList extends AbstractMenu {
    public CategoryList() {
        super("Список категорий");
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

        context.service.selectCategoryMenu(context, true);
        context.println("");
    }
}
