package com.careers.services;

import com.google.cloud.storage.*;
import com.careers.exception.ImageUploadException;
import com.careers.model.User;
import com.careers.repository.RedisUserRepository;
import com.careers.util.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Service
public class ImageService {
    private final Storage storage;
    private final RedisUserRepository redisUserRepository;
    private final String bucketName;

    // Constructor using Spring's dependency injection
    public ImageService(
            Storage storage,
            RedisUserRepository redisUserRepository,
            @Value("${gcs.bucket-name}") String bucketName
    ) {
        this.storage = storage;
        this.redisUserRepository = redisUserRepository;
        this.bucketName = bucketName;
    }

    public Mono<String> uploadAvatar(User user, FilePart filePart) {
        String objectId = UUID.randomUUID().toString();
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectId)
                .setContentType(filePart.headers().getContentType().toString())
                .build();

        return DataBufferUtils.join(filePart.content())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                })
                .flatMap(bytes -> Mono.fromCallable(() ->
                        storage.create(blobInfo, bytes)
                ))
                .doOnSuccess(blob -> updateUserImageMetadata(user, objectId))
                .map(blob -> objectId)
                .onErrorMap(e -> new ImageUploadException("Failed to upload image", e));
    }

    public Mono<String> getImageUrl(String imageId) {
        return redisUserRepository.getCachedImageUrl(imageId)
                .switchIfEmpty(Mono.fromCallable(() -> {
                    String url = generateSignedUrl(imageId);
                    redisUserRepository.cacheImageUrl(imageId, url).subscribe();
                    return url;
                }));
    }

    private void updateUserImageMetadata(User user, String imageId) {
        redisUserRepository.updateUserImage(user.getId(), imageId)
                .then(redisUserRepository.cacheImageUrl(imageId, generateSignedUrl(imageId)))
                .subscribe();
    }

    private String generateSignedUrl(String imageId) {
        return storage.signUrl(
                BlobInfo.newBuilder(bucketName, imageId).build(),
                7, // URL valid for 7 days
                Storage.SignUrlOption.withV4Signature()
        ).toString();
    }
}