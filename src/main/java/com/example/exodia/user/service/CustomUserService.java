package com.example.exodia.user.service;

import com.example.exodia.user.domain.CustomUserDetails;
import com.example.exodia.user.domain.User;
import com.example.exodia.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userNum) throws UsernameNotFoundException {
        User user = userRepository.findByUserNum(userNum)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자(" + userNum + ")를 찾을 수 없습니다."));
        return new CustomUserDetails(user);
    }
}
