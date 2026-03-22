package com.huuim.ecommerce.user.domain;

import com.huuim.ecommerce.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(nullable = false)
    private String loginPw;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    public User(String loginId, String loginPw, String name, UserRole role) {
        this.loginId = loginId;
        this.loginPw = loginPw;
        this.name = name;
        this.role = role;
    }
    /**
     * 왜:
     * - 비밀번호 변경 로직을 도메인 내부에 캡슐화
     * - 무분별한 Setter 사용 방지
     */
    public void updatePassword(String newPassword) {
        this.loginPw = newPassword;
    }
}