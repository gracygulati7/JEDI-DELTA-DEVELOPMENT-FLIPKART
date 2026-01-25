package com.flipfit.repository;

import com.flipfit.bean.FlipFitUser;
import java.util.HashMap;
import java.util.Map;

public class FlipFitUserRepository {

    // Using Integer (wrapper class) as HashMap keys cannot be primitive int
    // Mapping UserId to the FlipFitUser object
    private static Map<Integer, FlipFitUser> users = new HashMap<>();

    // Static block to initialize with some dummy data for testing
    static {
        users.put(1, new FlipFitUser(1, "John Doe"));
        users.put(2, new FlipFitUser(2, "Jane Smith"));

        // Setting initial values for dummy data
        users.get(1).setCity("Bangalore");
        users.get(1).setPincode(560001);
    }

    /**
     * Fetches a user by their unique ID
     * @param userId
     * @return FlipFitUser or null if not found
     */
    public FlipFitUser getUserById(int userId) {
        return users.get(userId);
    }

    /**
     * Updates an existing user in the map
     * @param user
     */
    public void updateUser(FlipFitUser user) {
        if (users.containsKey(user.getUserId())) {
            users.put(user.getUserId(), user);
        }
    }

    /**
     * Optional: Adds a new user to the repository
     * @param user
     */
    public void addUser(FlipFitUser user) {
        users.put(user.getUserId(), user);
    }
}