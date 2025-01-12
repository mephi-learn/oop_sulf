package info.sitnikov.finance.entity.menu;

import info.sitnikov.finance.model.Category;
import info.sitnikov.finance.model.Wallet;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class CategoryCreate extends AbstractMenu {
    public CategoryCreate() {
        super("Создание категории");
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

        String categoryName = context.selectString("Введите название для категории");
        Number budget = context.selectNumberDefault("Введите бюджет", 0);
        budget = Math.abs(budget.doubleValue()) * -1;

        // Создаём категорию
        Optional<Category> category = context.service.createCategoryInWallet(optionalWallet.get(), categoryName, budget.doubleValue());
        if (category.isEmpty()) {
            context.errorln("У вас уже имеется категория с таким названием: %s", categoryName);
            return;
        }

        // Сохраняем репозиторий
        context.service.storeRepository();

        context.println("КАТЕГОРИЯ СОЗДАНА");
        context.println("");
    }
}
