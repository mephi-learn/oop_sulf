package info.sitnikov.finance.entity.menu;

import info.sitnikov.finance.model.Category;

import java.util.Optional;

public final class CategoryEdit extends AbstractMenu {
    public CategoryEdit() {
        super("Редактирование категории");
    }

    @Override
    public void accept(Context context) {
        Optional<Category> optionalCategory = context.service.selectCategoryMenu(context, false);
        if (optionalCategory.isEmpty()) {
            return;
        }
        Category category = optionalCategory.get();

        String name = context.selectStringDefault("Введите название", category.getName());
        if (category.getName().equals("default") && !name.equals("default")) {
            context.errorln("Данную категорию нельзя переименовать");
            return;
        }

        Number budget = context.selectNumberDefault("Введите бюджет", category.getBudget());
        budget = Math.abs(budget.doubleValue()) * -1;

        context.service.editCategory(category, name, budget.doubleValue()).ifPresent(message -> {
            context.errorln("Ошибка редактирования. %s", message);
        });
        context.service.storeRepository();
    }
}
