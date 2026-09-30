package api.configs;

import api.models.CreateUserRequest;

import java.util.LinkedList;

public class SessionStorage {
    private static final ThreadLocal<SessionStorage> INSTANCE = ThreadLocal.withInitial(SessionStorage::new);
    private final LinkedList<CreateUserRequest> usersStorage = new LinkedList<>();

    private SessionStorage() {}

    public static ThreadLocal<SessionStorage> getInstance() {
        return INSTANCE;
    }

    public static void addUser(CreateUserRequest userRequest) {
        INSTANCE.get().usersStorage.add(userRequest);
    }

    public static CreateUserRequest getUser() {
        return INSTANCE.get().usersStorage.getFirst();
    }

    public static void clear() {
        INSTANCE.get().usersStorage.clear();
    }




}
