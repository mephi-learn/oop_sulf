package info.sitnikov.finance.model;

import lombok.Builder;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Optional;

@Data
@Builder
public class User {
    final String userId;
    String username;
    String hashedPassword;
    String lastWalletId;
    final Map<String, Wallet> wallets;

    public static String hash(String base) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            final StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                final String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean equalsSecret(String password) {
        if (!hashedPassword.isEmpty()) {
            return hashedPassword.equals(hash(password));
        }
        return false;
    }

    public double incomes() {
        return this.wallets.values().stream().map(Wallet::incomes).reduce(0.0, Double::sum);

    }

    public double expenses() {
        return this.wallets.values().stream().map(Wallet::expenses).reduce(0.0, Double::sum);
    }

    public Optional<Category> defaultWalletCategory() {
        for (var wallet : this.getWallets().values()) {
            if (wallet.getName().equals("default")) {
                for (var category : wallet.getCategories().values()) {
                    if (category.getName().equals("default")) {
                        return Optional.of(category);
                    }
                }
            }
        }
        return Optional.empty();
    }
}
