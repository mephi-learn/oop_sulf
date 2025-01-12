package info.sitnikov.finance.entity.menu;

import info.sitnikov.finance.model.Wallet;

import java.util.Optional;

public final class WalletDelete extends AbstractMenu {
    public WalletDelete() {
        super("Удаление кошелька");
    }

    @Override
    public void accept(Context context) {
        Optional<Wallet> optionalWallet = context.service.selectWalletMenu(context, false);
        if (optionalWallet.isEmpty()) {
            return;
        }
        Wallet wallet = optionalWallet.get();

        if (wallet.getName().equals("default")) {
            context.errorln("Данный кошелёк нельзя удалить");
            return;
        }

        String select = context.selectString("Вы уверены что хотите удалить?[y/N]");
        if (!select.equalsIgnoreCase("y")) {
            return;
        }

        if (!wallet.getCategories().isEmpty()) {
            context.println("Кошелёк не пуст. Если его удалить, автоматически удалятся все категории и платежи");
            select = context.selectString("Вы всё равно уверены что хотите удалить?[y/N]");
            if (!select.equalsIgnoreCase("y")) {
                return;
            }
        }

        context.service.removeWallet(wallet).ifPresent(message -> {
            context.errorln("Ошибка удаления. %s", message);
        });

        context.service.storeRepository();
    }
}
