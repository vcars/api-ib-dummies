package id.co.learn.ib.entity;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author  Adinandra Dharmasurya
 * @version 1.0
 * @since   2020-12-08
 */
@Data
@Entity
@Table(name = "customers", indexes = {
        @Index(name = "USERS_INDX_0", columnList = "username"),
        @Index(name = "USERS_INDX_1", columnList = "email"),
        @Index(name = "USERS_INDX_2", columnList = "fullname"),
        @Index(name = "USERS_INDX_4", columnList = "status"),
        @Index(name = "USERS_INDX_5", columnList = "password")
})
public class Customer extends BaseEntity implements Serializable {

    @Id
    @SequenceGenerator(name = "customer_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq")
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name="username", length = 30)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name="fullname")
    private String fullname;

    @Column(name="email")
    private String email;

    @Column(name = "status")
    private Integer status;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "wrong_password")
    private Integer wrongPassword;

    @Column(name = "updated_password_date")
    private Date updatedPasswordDate;

    @Column(name = "last_login")
    private Date lastLogin;

}