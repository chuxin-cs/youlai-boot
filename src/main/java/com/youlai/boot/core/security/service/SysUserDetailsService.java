package com.youlai.boot.core.security.service;

import com.youlai.boot.core.security.model.SysUserDetails;
import com.youlai.boot.system.model.dto.UserAuthInfo;
import com.youlai.boot.system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 系统用户认证 DetailsService
 *
 * @author Ray
 * @since 2021/10/19
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysUserDetailsService implements UserDetailsService {

    private final UserService userService;

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     * @throws UsernameNotFoundException 用户名未找到异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            UserAuthInfo userAuthInfo = userService.getUserAuthInfo(username);
            if (userAuthInfo == null) {
                throw new UsernameNotFoundException(username);
            }
            return new SysUserDetails(userAuthInfo);
        } catch (Exception e) {
            e.printStackTrace();
            // 记录异常日志
            log.error("认证异常:{}", e.getMessage());
            // 抛出异常
            throw e;
        }
    }
}
