package info.sitnikov.finance.service;

import info.sitnikov.finance.entity.menu.Context;
import info.sitnikov.finance.entity.security.Authentication;
import info.sitnikov.finance.model.Amount;
import info.sitnikov.finance.model.Category;
import info.sitnikov.finance.model.User;
import info.sitnikov.finance.model.Wallet;
import info.sitnikov.finance.repository.Repository;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.*;

public interface Service {
    Authentication getAuthentication();

    User createUser(String username, String password);

    List<Wallet> getWalletListByUserId(String userId);

    List<Amount> getAmountListByCategoryId(String categoryId);

    Optional<Wallet> createWalletInUser(User user, String walletName, String currency);

    Optional<Category> createCategoryInWallet(Wallet wallet, String categoryName, double budget);

    Optional<Amount> createAmountInCategory(Category category, String description, double amountSum, LocalDateTime dateTime);

    Optional<Wallet> getWallet(String walletId);

    Optional<String> editWallet(Wallet wallet, String newName, String currency);

    Optional<String> removeWallet(Wallet wallet);

    Optional<String> editCategory(Category category, String newName, double budget);

    Optional<String> removeCategory(Category category);

    Optional<String> editAmount(Amount amount, Category newCategory, String description, double amountSum, LocalDateTime dateTime);

    Optional<String> removeAmount(Amount amount);

    List<Category> getCategoryListByWalletId(String walletId);

    Optional<Wallet> selectWalletMenu(Context context);

    Optional<Category> selectCategoryMenu(Context context);

    Optional<Amount> selectAmountMenu(Context context, Category category);

    void storeRepository();

    final class Default implements Service {
        private final Repository repository;
        private final Authentication authentication;

        public Default(Repository repository, Authentication auth) {
            this.repository = repository;
            this.authentication = auth;
        }

        @Override
        public Authentication getAuthentication() {
            return this.authentication;
        }

        @Override
        public User createUser(String username, String password) {

            // Если пользователь с таким username уже присутствует, то выдаём ошибку
            if (repository.findUserByUserName(username).isPresent()) {
                throw new IllegalArgumentException("Username " + username + " already exists");
            }

            // Ищём уникальный идентификатор пользователя
            String userId;
            do {
                userId = UUID.randomUUID().toString();
            } while (this.repository.findUserByUserId(userId).isPresent());


            // Создаём пользователя
            User user = User.builder().userId(userId).username(username).hashedPassword(User.hash(password)).wallets(new HashMap<>()).build();
            return repository.storeUser(user).orElseThrow();
        }

        @Override
        public List<Wallet> getWalletListByUserId(String userId) {
            return repository.getWalletListByUserId(userId).stream().sorted(Comparator.comparing(Wallet::getName)).toList();
        }

        @Override
        public List<Amount> getAmountListByCategoryId(String categoryId) {
            return repository.getAmountListByCategoryId(categoryId).stream().sorted(Comparator.comparing(Amount::getDate)).toList();
        }

        @Override
        public Optional<Wallet> createWalletInUser(User user, String walletName, String currency) {
            // Не позволяем добавить пользователю два одинаковых кошелька
            for (var wallet : user.getWallets().entrySet()) {
                if (wallet.getValue().getName().equals(walletName)) {
                    return Optional.empty();
                }
            }

            Wallet wallet = new Wallet(user.getUserId(), walletName, currency);
            user.getWallets().put(wallet.getId(), wallet);
            return Optional.of(wallet);
        }

        @Override
        public Optional<Category> createCategoryInWallet(Wallet wallet, String categoryName, double budget) {
            // Не позволяем добавить в кошелёк две одинаковых категории
            for (var category : wallet.getCategories().entrySet()) {
                if (category.getValue().getName().equals(categoryName)) {
                    return Optional.empty();
                }
            }

            Category category = new Category(wallet.getId(), categoryName, budget);
            wallet.getCategories().put(category.getId(), category);
            return Optional.of(category);
        }

        @Override
        public Optional<Amount> createAmountInCategory(Category category, String description, double amountSum, LocalDateTime dateTime) {
            Amount amount = new Amount(category.getId(), description, amountSum, dateTime);
            category.getAmounts().put(amount.getId(), amount);
            return Optional.of(amount);
        }

        @Override
        public Optional<Wallet> getWallet(String walletId) {
            return repository.findWalletByWalletId(walletId);
        }

        @Override
        public Optional<String> editWallet(Wallet wallet, String newName, String currency) {
            Optional<User> optionalUser = repository.findUserByUserId(wallet.getUserId());
            if (optionalUser.isEmpty()) {
                return Optional.of("пользователь не найден");
            }
            User user = optionalUser.get();

            // Не позволяем добавить пользователю два одноимённых кошелька
            for (var existingWallet : user.getWallets().values()) {
                if (existingWallet.getName().equals(newName)) {
                    return Optional.of("кошелёк с таким именем уже существует");
                }
            }

            wallet.setName(newName);
            wallet.setCurrency(currency);
            return Optional.empty();
        }

        @Override
        public Optional<String> removeWallet(Wallet wallet) {
            Optional<User> optionalUser = repository.findUserByUserId(wallet.getUserId());
            if (optionalUser.isEmpty()) {
                return Optional.of("пользователь не найден");
            }
            User user = optionalUser.get();
            user.getWallets().remove(wallet.getId());
            return Optional.empty();
        }

        @Override
        public Optional<String> editCategory(Category category, String newName, double budget) {
            Optional<Wallet> optionalWallet = repository.findWalletByWalletId(category.getWalletId());
            if (optionalWallet.isEmpty()) {
                return Optional.of("кошелёк не найден");
            }
            Wallet wallet = optionalWallet.get();

            // Не позволяем добавить пользователю два одноимённых кошелька
            for (var existingWallet : wallet.getCategories().values()) {
                if (existingWallet.getName().equals(newName)) {
                    return Optional.of("категория с таким именем уже существует");
                }
            }

            category.setName(newName);
            category.setBudget(budget);
            return Optional.empty();
        }

        @Override
        public Optional<String> removeCategory(Category category) {
            Optional<Wallet> optionalWallet = repository.findWalletByWalletId(category.getWalletId());
            if (optionalWallet.isEmpty()) {
                return Optional.of("кошелёк не найден");
            }
            Wallet wallet = optionalWallet.get();
            wallet.getCategories().remove(category.getId());
            return Optional.empty();
        }

        @Override
        public Optional<String> editAmount(Amount amount, Category newCategory, String description, double amountSum, LocalDateTime dateTime) {

            // Если нужно переместить платёж из одной категории в другую
            if (!amount.getCategoryId().equals(newCategory.getId())) {
                Optional<Category> optionalCategory = repository.findCategoryByCategoryId(amount.getCategoryId());
                if (optionalCategory.isEmpty()) {
                    return Optional.of("категория для переноса платежа не найдена");
                }
                optionalCategory.get().removeAmountById(amount.getId());
                newCategory.addAmount(amount);
            }

            amount.setDescription(description);
            amount.setAmount(amountSum);
            amount.setDate(dateTime);
            return Optional.empty();
        }

        @Override
        public Optional<String> removeAmount(Amount amount) {
            return Optional.empty();
        }

        @Override
        public List<Category> getCategoryListByWalletId(String walletId) {
            return repository.getCategoryListByWalletId(walletId).stream().sorted(Comparator.comparing(Category::getName)).toList();
        }

        @Override
        public void storeRepository() {
            this.repository.store();
        }

        @NotNull
        public String generateShort(int limit) {
            char[] table = "012345789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
            String shortName;
            do {
                shortName = generateRandomString(table, limit);
            } while (shortName.charAt(0) < 'a' || shortName.charAt(0) > 'z');
            return shortName;
        }

        @NotNull
        public String generateUsername() {
            char[] table = "abcdefghijklmnopqrstuvwxyz".toCharArray();
            return generateRandomString(table, 10);
        }

        @NotNull
        public String generatePassword() {
            char[] table = "012345789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()".toCharArray();
            return generateRandomString(table, 10);
        }

        @NotNull
        private String generateRandomString(char[] table, int limit) {
            Random random = new Random(new Date().getTime());
            StringBuilder buffer = new StringBuilder(limit);
            for (int i = 0; i < limit; i++) {
                char value = table[random.nextInt(table.length)];
                buffer.append(value);
            }
            return buffer.toString();
        }

        public Optional<Wallet> selectWalletMenu(Context context) {
            // Получаем аутентифицированного пользователя
            Optional<User> sessionUser = context.authorized().map(Authentication.Session::user);

            // Если пользователь не аутентифицирован, то сообщаем, что данный пункт меню для него недоступен
            if (sessionUser.isEmpty()) {
                context.errorln("Требуется аутентификация");
                return Optional.empty();
            }

            User user = sessionUser.get();

            List<Wallet> wallets = getWalletListByUserId(user.getUserId());
            for (int i = 0; i < wallets.size(); i++) {
                Wallet wallet = wallets.get(i);
                context.println("%4d: [%s]", i + 1, wallet.getName());
            }
            context.println("%4d: %s", wallets.size() + 1, "Назад");
            context.printLine();
            context.print("> ");

            String in = context.inputString();
            Wallet wallet;

            // Если был выбран последний пункт (назад), то выходим
            if (in.equals(String.valueOf(wallets.size() + 1))) {
                return Optional.empty();
            }

            try {
                int index = Integer.parseInt(in) - 1;
                if (index >= 0 && index < wallets.size()) {
                    wallet = wallets.get(index);
                } else {
                    context.errorln("Неверный номер. %s", in);
                    return Optional.empty();
                }
            } catch (Exception ex) {
                context.errorln("Ошибка ввода. %s", in);
                return Optional.empty();
            }

            return Optional.of(wallet);
        }

        @Override
        public Optional<Category> selectCategoryMenu(Context context) {
            // Получаем выбранный кошелёк
            Optional<Wallet> sessionWallet = context.authorized().map(Authentication.Session::wallet);

            // Если кошелёк не выбран, то сообщаем, что данный пункт меню недоступен
            if (sessionWallet.isEmpty()) {
                context.errorln("Необходимо выбрать кошелёк");
                return Optional.empty();
            }

            Wallet wallet = sessionWallet.get();

            List<Category> categories = getCategoryListByWalletId(wallet.getId());
            for (int i = 0; i < categories.size(); i++) {
                Category category = categories.get(i);
                context.println("%4d: %s", i + 1, category.getName());
            }
            context.println("%4d: %s", categories.size() + 1, "Назад");
            context.printLine();
            context.print("> ");

            String in = context.inputString();
            Category category;

            // Если был выбран последний пункт (назад), то выходим
            if (in.equals(String.valueOf(categories.size() + 1))) {
                return Optional.empty();
            }

            try {
                int index = Integer.parseInt(in) - 1;
                if (index >= 0 && index < categories.size()) {
                    category = categories.get(index);
                } else {
                    context.errorln("Неверный номер. %s", in);
                    return Optional.empty();
                }
            } catch (Exception ex) {
                context.errorln("Ошибка ввода. %s", in);
                return Optional.empty();
            }

            return Optional.of(category);
        }

        @Override
        public Optional<Amount> selectAmountMenu(Context context, Category category) {

            List<Amount> amounts = getAmountListByCategoryId(category.getId());
            for (int i = 0; i < amounts.size(); i++) {
                Amount amount = amounts.get(i);
                context.println("%4d: [%d] [%s] [%s]", i + 1, amount.getAmount(), amount.getDescription(), amount.getDate());
            }
            context.println("%4d: %s", amounts.size() + 1, "Назад");
            context.printLine();
            context.print("> ");

            String in = context.inputString();
            Amount amount;

            // Если был выбран последний пункт (назад), то выходим
            if (in.equals(String.valueOf(amounts.size() + 1))) {
                return Optional.empty();
            }

            try {
                int index = Integer.parseInt(in) - 1;
                if (index >= 0 && index < amounts.size()) {
                    amount = amounts.get(index);
                } else {
                    context.errorln("Неверный номер. %s", in);
                    return Optional.empty();
                }
            } catch (Exception ex) {
                context.errorln("Ошибка ввода. %s", in);
                return Optional.empty();
            }

            return Optional.of(amount);
        }
    }
}
