package com.example.awsimageupload.profile;

import com.amazonaws.services.s3.model.Bucket;
import com.example.awsimageupload.bucket.BucketName;
import com.example.awsimageupload.filestore.FileStore;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class UserProfileService {

    private final FakeUserProfileDataStore fakeUserProfileDataStore;
    private final FileStore fileStore;

    @Autowired
    public UserProfileService(FakeUserProfileDataStore fakeUserProfileDataStore, FileStore fileStore) {
        this.fakeUserProfileDataStore = fakeUserProfileDataStore;
        this.fileStore = fileStore;
    }

    public List<UserProfile> getUserProfiles() {
        return fakeUserProfileDataStore.getUserProfiles();
    }

    public void uploadUserProfileImage(UUID userProfileId, MultipartFile file) {

        isFile(file);

        isImage(file);

        UserProfile userProfile = getUserProfile(userProfileId);

        Map<String, String> metaData = extractMetaData(file);

        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(), userProfile.getUserProfileId());
        String fileName = String.format("%s-%s", file.getOriginalFilename(), UUID.randomUUID());

        try {
            fileStore.save(path,fileName,Optional.of(metaData),file.getInputStream());
            userProfile.setUserProfileImageLink(fileName);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }


    }

    private static void isFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalStateException(("File is empty"));
        }
    }

    private static void isImage(MultipartFile file) {
        if (!Arrays.asList(ContentType.IMAGE_JPEG.getMimeType(), ContentType.IMAGE_PNG.getMimeType()).contains(file.getContentType())) {
            throw new IllegalStateException(("File is not image"));
        }
    }

    private UserProfile getUserProfile(UUID userProfileId) {
        return fakeUserProfileDataStore.getUserProfiles()
                .stream()
                .filter(user -> user.getUserProfileId().equals(userProfileId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(("UserProfile not found")));
    }

    private static Map<String, String> extractMetaData(MultipartFile file) {
        Map<String, String> metaData = new HashMap<>();
        metaData.put("Content-Type", file.getContentType());
        metaData.put("Content-Length",String.valueOf(file.getSize()));
        return metaData;
    }

    public byte[] downloadUserProfileImage(UUID userProfileId) {

        UserProfile userProfile = getUserProfile(userProfileId);
        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(),
                userProfile.getUserProfileId());

      return userProfile.getUserProfileImageLink()
                .map(link->fileStore.download(path,link))
              .orElse(new byte[0]);
    }
}
