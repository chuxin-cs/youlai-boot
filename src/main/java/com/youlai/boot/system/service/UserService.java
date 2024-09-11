package com.youlai.boot.system.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.boot.common.enums.ContactType;
import com.youlai.boot.system.model.dto.UserAuthInfo;
import com.youlai.boot.system.model.dto.UserExportDTO;
import com.youlai.boot.system.model.entity.User;
import com.youlai.boot.system.model.query.UserPageQuery;
import com.youlai.boot.system.model.vo.UserInfoVO;
import com.youlai.boot.system.model.vo.UserPageVO;
import com.youlai.boot.system.model.vo.UserProfileVO;
import com.youlai.boot.system.model.form.*;

import java.util.List;

/**
 * 用户业务接口
 *
 * @author haoxr
 * @since 2022/1/14
 */
public interface UserService extends IService<User> {

    /**
     * 用户分页列表
     *
     * @return
     */
    IPage<UserPageVO> listPagedUsers(UserPageQuery queryParams);


    /**
     * 获取用户表单数据
     *
     * @param userId
     * @return
     */
    UserForm getUserFormData(Long userId);


    /**
     * 新增用户
     *
     * @param userForm 用户表单对象
     * @return
     */
    boolean saveUser(UserForm userForm);

    /**
     * 修改用户
     *
     * @param userId   用户ID
     * @param userForm 用户表单对象
     * @return
     */
    boolean updateUser(Long userId, UserForm userForm);


    /**
     * 删除用户
     *
     * @param idsStr 用户ID，多个以英文逗号(,)分割
     * @return
     */
    boolean deleteUsers(String idsStr);


    /**
     * 根据用户名获取认证信息
     *
     * @param username 用户名
     * @return {@link UserAuthInfo}
     */

    UserAuthInfo getUserAuthInfo(String username);


    /**
     * 获取导出用户列表
     *
     * @param queryParams 查询参数
     * @return
     */
    List<UserExportDTO> listExportUsers(UserPageQuery queryParams);


    /**
     * 获取登录用户信息
     *
     * @return
     */
    UserInfoVO getCurrentUserInfo();

    /**
     * 获取个人中心用户信息
     *
     * @return
     */
    UserProfileVO getUserProfile(Long userId);

    /**
     * 修改个人中心用户信息
     *
     * @param formData 表单数据
     * @return
     */
    boolean updateUserProfile(UserProfileForm formData);

    /**
     * 修改用户密码
     *
     * @param userId 用户ID
     * @param data   修改密码表单数据
     * @return
     */
    boolean changePassword(Long userId, PasswordChangeForm data);

    /**
     * 重置用户密码
     *
     * @param userId   用户ID
     * @param password 重置后的密码
     * @return
     */
    boolean resetPassword(Long userId, String password);

    /**
     * 发送验证码
     *
     * @param contact 联系方式
     * @param type    联系方式类型
     * @return
     */
    boolean sendVerificationCode(String contact, ContactType type);

    /**
     * 修改当前用户手机号
     *
     * @param data 表单数据
     * @return
     */
    boolean bindMobile(MobileBindingForm data);

    /**
     * 修改当前用户邮箱
     *
     * @param data 表单数据
     * @return
     */
    boolean bindEmail(EmailChangeForm data);
}
