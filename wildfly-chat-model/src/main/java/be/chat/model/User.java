package be.chat.model;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@ToString
@Getter
@Entity
@Table(name = "poc_user")
public class User {

    @Id
    private String login;

    private String password;

    @Column(name = "hash_password")
    private String hashPassword;

}
