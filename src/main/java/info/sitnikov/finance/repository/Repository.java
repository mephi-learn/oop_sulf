package info.sitnikov.finance.repository;

import info.sitnikov.finance.adapter.Storage;
import info.sitnikov.finance.model.Amount;
import info.sitnikov.finance.model.Category;
import info.sitnikov.finance.model.User;
import info.sitnikov.finance.model.Wallet;

import java.util.*;

public interface Repository {
    Optional<User> storeUser(User user);

    Optional<User> findUserByUserName(String username);

    Optional<User> findUserByUserId(String userId);

    Optional<Wallet> findWalletByWalletId(String walletId);

    Optional<Category> findCategoryByCategoryId(String categoryId);

    Optional<Map<String, Wallet>> getWalletMapByUserId(String userId);

    List<Wallet> getWalletListByUserId(String userId);

    List<Category> getCategoryListByWalletId(String walletId);

    List<Amount> getAmountListByCategoryId(String categoryId);


    void store();

    void load();

    public class Memory implements Repository {
        private final Storage storage;
        private final Map<String, User> users = new HashMap<>();

        public Memory(Storage storage) {
            this.storage = storage;
        }

        @Override
        public Optional<User> storeUser(User user) {
            this.users.put(user.getUserId(), user);
            this.store();
            return Optional.of(user);
        }

        @Override
        public Optional<User> findUserByUserName(String username) {
            for (User user : this.users.values()) {
                if (user.getUsername().equals(username)) {
                    return Optional.of(user);
                }
            }
            return Optional.empty();
        }

        @Override
        public Optional<User> findUserByUserId(String userId) {
            User user = this.users.get(userId);
            return Optional.ofNullable(user);
        }

        @Override
        public Optional<Wallet> findWalletByWalletId(String walletId) {
            for (var user : this.users.values()) {
                for (var wallet : user.getWallets().values()) {
                    if (wallet.getId().equals(walletId)) {
                        return Optional.of(wallet);
                    }
                }
            }
            return Optional.empty();
        }

        @Override
        public Optional<Category> findCategoryByCategoryId(String categoryId) {
            for (var user : this.users.values()) {
                for (var wallet : user.getWallets().values()) {
                    for (var category : wallet.getCategories().values()) {
                        if (category.getId().equals(categoryId)) {
                            return Optional.of(category);
                        }
                    }
                }
            }
            return Optional.empty();
        }

        @Override
        public List<Category> getCategoryListByWalletId(String walletId) {
            Optional<Wallet> optionalWallet = findWalletByWalletId(walletId);
            return optionalWallet.map(wallet -> new ArrayList<>(wallet.getCategories().values())).orElseGet(ArrayList::new);
        }

        @Override
        public List<Amount> getAmountListByCategoryId(String categoryId) {
            Optional<Category> optionalCategory = findCategoryByCategoryId(categoryId);
            return optionalCategory.map(category -> new ArrayList<>(category.filterAmounts())).orElseGet(ArrayList::new);
        }

        @Override
        public List<Wallet> getWalletListByUserId(String userId) {
            User user = this.users.get(userId);
            return new ArrayList<>(user.getWallets().values());
        }

        @Override
        public Optional<Map<String, Wallet>> getWalletMapByUserId(String userId) {
            User user = this.users.get(userId);
            if (user == null) {
                return Optional.empty();
            }
            return Optional.of(new HashMap<>(user.getWallets()));
        }

        @Override
        public void store() {
            try {
                storage.store(this.users);
            } catch (Exception ignored) {
            }
        }

        @Override
        public void load() {
            try {
                this.users.putAll(this.storage.load());
            } catch (Exception e) {
                this.users.clear();
                this.store();
            }
        }
    }
}
