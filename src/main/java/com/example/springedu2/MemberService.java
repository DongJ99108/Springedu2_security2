package com.example.springedu2;

import com.example.springedu2.dto.MemberCreateForm;
import com.example.springedu2.entity.Member;
import com.example.springedu2.entity.Role;
import com.example.springedu2.repository.MemberRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 회원정보 db 에서 회원이름으로 조회
        Member member = memberRepository.findByUsername( username )
                 .orElseThrow( () -> new UsernameNotFoundException(
                        "존재하지 않는 사용자입니다") );

        // 조회한 결과를 Member -> UserDetails 으로 변환
        UserDetails user = User.builder()
                .username(member.getUsername())        // 사용자 id
                .password(member.getPassword())        // 사용자 비밀번호
                .disabled(!member.isEnabled())          // 계정 사용 가능
                .roles(member.getRole().toString())    // 사용자 권한 "ADMIN" -> ROLE_ADMIN 권한
                .build();
        return  user;
    }

    // ---------------------------------------------------

    // 일반 유저 회원가입
    public Member register(MemberCreateForm memberForm) {
        memberForm.setRole(Role.USER.name());
        return create(memberForm);
    }

    // 회원가입
    @Transactional
    private Member create(MemberCreateForm memberForm) {
        // 기존 회원인지 조회
        validNewMember(memberForm.getUsername(), memberForm.getEmail());

        Member member = new Member();
        member.setUsername( memberForm.getUsername());
        member.setPassword(passwordEncoder.encode(memberForm.getPassword()));
        member.setName(memberForm.getName());
        member.setEmail(memberForm.getEmail());
        member.setRole( parseRole(member.getRole().name()) );
        member.setEnabled( true );

        return memberRepository.save(member); // save 괄호안에는 Entity 만 올 수 있음(memberForm은 Entity 가 안붙은 놈이기 때문에 안됨)
    }

    // 기존 회원인지 체크
    private void validNewMember(String username, String email) {
        if (memberRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }
        if(memberRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }
    }

    // 권한 문자열 변환 "ADMIN" -> Role.ADMIN
    private Role parseRole(String role) {
        if (role == null || role.isBlank()) {
            return Role.USER;
        }
        return Role.valueOf(role.toUpperCase());
    }

}
