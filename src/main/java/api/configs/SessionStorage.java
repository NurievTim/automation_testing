package api.configs;

import api.models.CreateUserRequest;

import java.util.LinkedList;

public class SessionStorage {
    private static final SessionStorage INSTANCE = new SessionStorage();
    private final LinkedList<CreateUserRequest> usersStorage = new LinkedList<>();

    private SessionStorage() {}

    public static SessionStorage getInstance() {
        return INSTANCE;
    }

    public static void addUser(CreateUserRequest userRequest) {
        INSTANCE.usersStorage.add(userRequest);
    }

    public static CreateUserRequest getUser() {
        return INSTANCE.usersStorage.getFirst();
    }

    public static void clear() {
        INSTANCE.usersStorage.clear();
    }




}
