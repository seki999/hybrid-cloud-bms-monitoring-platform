package com.example.bms.security;

import com.example.bms.common.persistence.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/** 管理画面显示的用户主数据；生产认证应委托企业 IdP，本地密码不写入此表。 */
@Entity
@Table(name = "app_users")
public class AppUser extends AuditableEntity {
    @Column(nullable = false, unique = true, length = 80)
    private String username;
    @Column(name = "display_name", nullable = false, length = 120)
    private String displayName;
    @Column(nullable = false, length = 40)
    private String role;
    @Column(nullable = false)
    private boolean enabled;

    protected AppUser() { }
    public AppUser(String username, String displayName, String role, boolean enabled) {
        this.username = username;
        this.displayName = displayName;
        this.role = role;
        this.enabled = enabled;
    }
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public String getRole() { return role; }
    public boolean isEnabled() { return enabled; }
}

