package org.example.entity;

import jakarta.persistence.*;

@Table(name = "usersprofile")
@Entity
public class UserProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(name = "user_vk_id")
    private Long userVkId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "avatar", length = 512)
    private String avatar;

    @Column(name = "sex")
    private String sex;

    public UserProfileEntity() {}


    public UserProfileEntity(Long id, Long userVkId, String firstName, String lastName, String avatar, String sex) {
        this.id = id;
        this.userVkId = userVkId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar = avatar;
        this.sex = sex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserVkId() {
        return userVkId;
    }

    public void setUserVkId(Long userVkId) {
        this.userVkId = userVkId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }



}
