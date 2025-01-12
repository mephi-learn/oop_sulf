package info.sitnikov.finance.entity.menu;

import info.sitnikov.finance.model.Category;

import java.util.Optional;

public final class CategoryDelete extends AbstractMenu {
    public CategoryDelete() {
        super("Удаление категории");
    }

    @Override
    public void accept(Context context) {
        Optional<Category> optionalCategory = context.service.selectCategoryMenu(context);
        if (optionalCategory.isEmpty()) {
            return;
        }
        Category category = optionalCategory.get();

        if (category.getName().equals("default")) {
            context.errorln("Данную категорию нельзя удалить");
            return;
        }

        String select = context.selectString("Вы уверены что хотите удалить?[y/N]");
        if (!select.equalsIgnoreCase("y")) {
            return;
        }

        if (!category.getAmounts().isEmpty()) {
            context.println("Категория не пуста. Если её удалить, все платежи автоматически удалятся");
            select = context.selectString("Вы всё равно уверены что хотите удалить?[y/N]");
            if (!select.equalsIgnoreCase("y")) {
                return;
            }
        }

        context.service.removeCategory(category).ifPresent(message -> {
            context.errorln("Ошибка удаления. %s", message);
        });

        context.service.storeRepository();
    }
}
