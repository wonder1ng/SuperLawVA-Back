package com.superlawva.domain.user.service;

import com.superlawva.domain.user.dto.UserRequestDTO;
import com.superlawva.domain.user.dto.UserResponseDTO;
import com.superlawva.domain.user.entity.User;
import com.superlawva.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream()
                .map(UserResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO findById(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("회원이 없습니다."));
        return UserResponseDTO.from(u);
    }

    @Override
    public UserResponseDTO create(UserRequestDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .build();
        User saved = userRepository.save(user);
        return UserResponseDTO.from(saved);
    }

    @Override
    public UserResponseDTO update(Long id, UserRequestDTO dto) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("회원이 없습니다."));
        u.changeEmail(dto.getEmail());
        u.changeNickname(dto.getNickname());
        User saved = userRepository.save(u);
        return UserResponseDTO.from(saved);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void processOAuth2User(String registrationId, Map<String,Object> attributes) {
        String email = (String) attributes.get("email");
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isEmpty()) {
            User u = User.builder()
                    .email(email)
                    .nickname((String) attributes.get("nickname"))
                    .kakaoId(registrationId.equals("kakao")
                            ? Long.valueOf((String) attributes.get("id"))
                            : null)
                    .build();
            userRepository.save(u);
        } else {
            User u = existing.get();
            String newNick = (String) attributes.get("nickname");
            if (newNick != null && !newNick.equals(u.getNickname())) {
                u.changeNickname(newNick);
            }
        }
    }
}
