package info.sitnikov.finance.entity.menu;

import info.sitnikov.finance.entity.security.Authentication;
import info.sitnikov.finance.model.Category;
import info.sitnikov.finance.model.Wallet;

import java.util.Optional;

public final class AmountList extends AbstractMenu {
    public AmountList() {
        super("Список платежей");
    }

    @Override
    public void accept(Context context) {
        // Выбираем категорию
        Optional<Category> optionalCategory = context.service.selectCategoryMenu(context, false);
        if (optionalCategory.isEmpty()) {
            return;
        }

        context.service.selectAmountMenu(context, optionalCategory.get(), true);
        context.println("");
    }
}
