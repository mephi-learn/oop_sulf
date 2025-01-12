package info.sitnikov.finance.entity.security;

import info.sitnikov.finance.model.User;
import info.sitnikov.finance.model.Wallet;
import info.sitnikov.finance.repository.Repository;

import java.util.Optional;

public interface Authentication {

    static Authentication create(Repository repository) {
        return new Default(repository);
    }

    Optional<Session> authenticate(String username, String password);

    Optional<Session> authenticateByUserId(String userId);

    interface Session {

        static Session of(User user) {
            return new SimpleSession(user);
        }

        User user();

        void setWallet(Wallet wallet);

        Wallet wallet();
    }

    final class SimpleSession implements Session {
        final private User user;
        private Wallet wallet;

        public SimpleSession(User user) {
            this.user = user;
        }

        @Override
        public User user() {
            return user;
        }

        @Override
        public void setWallet(Wallet wallet) {
            this.wallet = wallet;
        }

        @Override
        public Wallet wallet() {
            return this.wallet;
        }
    }

    final class Default implements Authentication {
        private final Repository repository;

        private Default(Repository repository) {
            this.repository = repository;
        }

        @Override
        public Optional<Session> authenticate(String username, String password) {
            Optional<User> user = repository.findUserByUserName(username);
            if (user.isPresent() && user.get().equalsSecret(password)) {
                return Optional.of(new SimpleSession(user.get()));
            }
            return Optional.empty();
        }

        @Override
        public Optional<Session> authenticateByUserId(String userId) {
            return repository.findUserByUserId(userId).map(SimpleSession::new);
        }
    }
}
