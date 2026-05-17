package team.themoment.everygsm.server.v2.domain.user.entity;

import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.themoment.everygsm.server.v2.domain.user.entity.constant.Role;

@Entity
@Table(name = "users", indexes = @Index(name = "idx_users_name", columnList = "name"))
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class UserJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(name = "student_number", nullable = false, length = 4)
    private String studentNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    public UserJpaEntity(String email, String name, String studentNumber, Role role) {
        this.email = email;
        this.name = name;
        this.studentNumber = studentNumber;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> thisClass = (this instanceof HibernateProxy hp)
                ? hp.getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        Class<?> otherClass = (o instanceof HibernateProxy hp)
                ? hp.getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        if (!thisClass.equals(otherClass)) {
            return false;
        }
        UserJpaEntity other = (UserJpaEntity) o;
        return this.id != null && Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return (this instanceof HibernateProxy hp)
                ? hp.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
