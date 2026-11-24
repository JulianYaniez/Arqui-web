package com.arquiweb.identity.dev.service;

import com.arquiweb.identity.auth.domain.User;
import com.arquiweb.identity.auth.domain.enums.UserRole;
import com.arquiweb.identity.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DevService {

    private final UserRepository userRepository;
    private final String DEFAULT_PASSWORD = "Pass_1234";
    private final String DEFAULT_FIRST_NAME = "FIRST_NAME";
    private final String DEFAULT_LAST_NAME = "LAST_NAME";
    private final PasswordEncoder passwordEncoder;

    public void seed() {

        User admin = User.registerClient(
                "admin@gmail.com",
                DEFAULT_FIRST_NAME,
                DEFAULT_LAST_NAME,
                DEFAULT_PASSWORD,
                DEFAULT_PASSWORD,
                passwordEncoder
        );
        admin.setRole(UserRole.ADMIN);

        User organizer = User.registerClient(
                "organizer@gmail.com",
                DEFAULT_FIRST_NAME,
                DEFAULT_LAST_NAME,
                DEFAULT_PASSWORD,
                DEFAULT_PASSWORD,
                passwordEncoder
        );
        organizer.setRole(UserRole.ORGANIZER);

        User staff = User.registerClient(
                "staff@gmail.com",
                DEFAULT_FIRST_NAME,
                DEFAULT_LAST_NAME,
                DEFAULT_PASSWORD,
                DEFAULT_PASSWORD,
                passwordEncoder
        );
        staff.setRole(UserRole.ACCESS_STAFF);

        User user = User.registerClient(
                "user@gmail.com",
                DEFAULT_FIRST_NAME,
                DEFAULT_LAST_NAME,
                DEFAULT_PASSWORD,
                DEFAULT_PASSWORD,
                passwordEncoder
        );

        userRepository.save(admin);
        userRepository.save(organizer);
        userRepository.save(staff);
        userRepository.save(user);
    }

}
