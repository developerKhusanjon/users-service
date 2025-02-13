package com.careers.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final UserInformationRepository infoRepo;
    private final UserDocumentRepository userDocRepo;

    public Mono<User> createUserWithInfo(User user, UserInformation info) {
        return userRepo.save(user)
            .flatMap(savedUser -> infoRepo.save(info.setUserId(savedUser.getId()))
            .flatMap(info -> userDocRepo.save(convertToDocument(user, info)))
            .thenReturn(user);
    }

    private UserDocument convertToDocument(User user, UserInformation info) {
        return new UserDocument()
            .setUserId(user.getId().toString())
            .setEmail(user.getEmail())
            .setPhone(user.getPhone())
            .setFirstName(info.getFirstName())
            .setLastName(info.getLastName())
            .setTitle(info.getTitle());
    }
}