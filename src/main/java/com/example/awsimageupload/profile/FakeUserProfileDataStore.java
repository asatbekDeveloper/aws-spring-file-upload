package com.example.awsimageupload.profile;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FakeUserProfileDataStore {

    private static final List<UserProfile> USER_PROFILES = new ArrayList<>();

    static {
        USER_PROFILES.add(new UserProfile(UUID.fromString("40dbbf50-05fe-4bcc-9a31-e09c58a9777a"), "asadbek", null));
        USER_PROFILES.add(new UserProfile(UUID.fromString("a31df348-eb2b-433f-ae3e-cf32cb7d4bf8"), "bek", null));
    }

    public List<UserProfile> getUserProfiles() {
        return USER_PROFILES;
    }

}
