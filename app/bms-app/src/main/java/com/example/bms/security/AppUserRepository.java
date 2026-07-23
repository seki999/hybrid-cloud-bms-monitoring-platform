package com.example.bms.security;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** 用户管理画面使用的主数据访问层。 */
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    List<AppUser> findAllByOrderByUsernameAsc();
}

