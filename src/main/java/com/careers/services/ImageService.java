package com.careers.services;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ImageService {
    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    public Mono<String> uploadAvatar(Mono<FilePart> filePart) {
        return filePart.flatMap(fp -> {
            String objectId = UUID.randomUUID().toString();
            BlobInfo blobInfo = BlobInfo.newBuilder("avatars-bucket", objectId).build();
            return Mono.fromCallable(() -> 
                storage.create(blobInfo, fp.content().readAllBytes())
            ).thenReturn(objectId);
        });
    }
    
    public String getImageUrl(String imageId) {
        return "https://storage.googleapis.com/avatars-bucket/" + imageId;
    }
}