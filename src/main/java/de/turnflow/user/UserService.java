package de.turnflow.user;

import de.turnflow.common.exception.BusinessException;
import de.turnflow.common.exception.ErrorCode;
import de.turnflow.common.exception.NotFoundException;

import de.turnflow.member.MemberRepository;
import de.turnflow.member.entity.Member;
import de.turnflow.user.dto.CreateUserRequest;
import de.turnflow.user.dto.UpdateUserRequest;
import de.turnflow.user.dto.UserDto;
import de.turnflow.user.entity.Role;
import de.turnflow.user.entity.RoleName;
import de.turnflow.user.entity.UserAccount;
import de.turnflow.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserDto create(CreateUserRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        UserAccount user = UserAccount.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .build();

        if (request.getMemberId() != null) {
            Member member = memberRepository.findById(request.getMemberId())
                    .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND, request.getMemberId() ));
            user.setMember(member);
        }

        user.setRoles(resolveRoles(request.getRoles()));

        return userMapper.toDto(userRepository.save(user));
    }

    public UserDto update(Long id, UpdateUserRequest request) {

        UserAccount user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, id));

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }

        if (request.getMemberId() != null) {
            Member member = memberRepository.findById(request.getMemberId())
                    .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND, request.getMemberId()));
            user.setMember(member);
        }

        if (request.getRoles() != null) {
            user.setRoles(resolveRoles(request.getRoles()));
        }

        return userMapper.toDto(userRepository.save(user));
    }

    public UserDto findById(Long id) {
        return userMapper.toDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, id)));
    }

    public Set<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toSet());
    }

    private Set<Role> resolveRoles(Set<String> roleNames) {
        return roleNames.stream()
                .map(name -> {
                    try {
                        return RoleName.valueOf(name);
                    } catch (IllegalArgumentException e) {
                        throw  new NotFoundException(ErrorCode.INVALID_ROLE, name);
                    }
                })
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new NotFoundException(ErrorCode.INVALID_ROLE, roleName)))
                .collect(Collectors.toSet());
    }
}