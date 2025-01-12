package info.sitnikov.finance.entity.menu;

import info.sitnikov.finance.model.Wallet;

import java.util.Optional;

public final class WalletEdit extends AbstractMenu {
    public WalletEdit() {
        super("Редактирование кошелька");
    }

    @Override
    public void accept(Context context) {
        Optional<Wallet> optionalWallet = context.service.selectWalletMenu(context, false);
        if (optionalWallet.isEmpty()) {
            return;
        }
        Wallet wallet = optionalWallet.get();

        String name = context.selectStringDefault("Введите название", wallet.getName());
        if (wallet.getName().equals("default") && !name.equals("default")) {
            context.errorln("Данный кошелёк нельзя переименовывать");
            return;
        }

        String currency = context.selectStringDefault("Введите валюту", wallet.getCurrency());
        context.service.editWallet(wallet, name, currency).ifPresent(message -> {
            context.errorln("Ошибка редактирования. %s", message);
        });
        context.service.storeRepository();
    }
}
