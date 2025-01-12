package info.sitnikov.finance.entity.menu;

import info.sitnikov.finance.model.Wallet;

import java.util.Optional;

public final class WalletSelect extends AbstractMenu {
    public WalletSelect() {
        super("Выбор кошелька");
    }

    @Override
    public void accept(Context context) {
        Optional<Wallet> optionalWallet = context.service.selectWalletMenu(context, false);
        if (optionalWallet.isEmpty()) {
            return;
        }
        Wallet wallet = optionalWallet.get();

        context.authorized().ifPresent(session -> {
            session.setWallet(wallet);
            session.user().setLastWalletId(wallet.getId());
        });
    }
}
