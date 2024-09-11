package com.youlai.boot.system.listener;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youlai.boot.common.base.BaseAnalysisEventListener;
import com.youlai.boot.system.model.entity.Dept;
import com.youlai.boot.system.model.entity.Role;
import com.youlai.boot.system.model.entity.User;
import com.youlai.boot.system.model.entity.UserRole;
import com.youlai.boot.common.base.IBaseEnum;
import com.youlai.boot.common.constant.SystemConstants;
import com.youlai.boot.common.enums.GenderEnum;
import com.youlai.boot.common.enums.StatusEnum;
import com.youlai.boot.system.converter.UserConverter;
import com.youlai.boot.system.model.dto.UserImportDTO;
import com.youlai.boot.system.service.DeptService;
import com.youlai.boot.system.service.RoleService;
import com.youlai.boot.system.service.UserRoleService;
import com.youlai.boot.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户导入监听器
 * <p>
 * <a href="https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read#%E6%9C%80%E7%AE%80%E5%8D%95%E7%9A%84%E8%AF%BB%E7%9A%84%E7%9B%91%E5%90%AC%E5%99%A8">最简单的读的监听器</a>
 *
 * @author Ray
 * @since 2022/4/10
 */
@Slf4j
public class UserImportListener extends BaseAnalysisEventListener<UserImportDTO> {


    // 有效条数
    private int validCount;

    // 无效条数
    private int invalidCount;

    // 导入返回信息
    StringBuilder msg = new StringBuilder();

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserConverter userConverter;
    private final RoleService roleService;
    private final UserRoleService userRoleService;
    private final DeptService deptService;

    public UserImportListener() {
        this.userService = SpringUtil.getBean(UserService.class);
        this.passwordEncoder = SpringUtil.getBean(PasswordEncoder.class);
        this.roleService = SpringUtil.getBean(RoleService.class);
        this.userRoleService = SpringUtil.getBean(UserRoleService.class);
        this.deptService = SpringUtil.getBean(DeptService.class);
        this.userConverter = SpringUtil.getBean(UserConverter.class);
    }

    /**
     * 每一条数据解析都会来调用
     * <p>
     * 1. 数据校验；全字段校验
     * 2. 数据持久化；
     *
     * @param userImportDTO 一行数据，类似于 {@link AnalysisContext#readRowHolder()}
     */
    @Override
    public void invoke(UserImportDTO userImportDTO, AnalysisContext analysisContext) {
        log.info("解析到一条用户数据:{}", JSONUtil.toJsonStr(userImportDTO));
        // 校验数据
        StringBuilder validationMsg = new StringBuilder();

        String username = userImportDTO.getUsername();
        if (StrUtil.isBlank(username)) {
            validationMsg.append("用户名为空；");
        } else {
            long count = userService.count(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
            if (count > 0) {
                validationMsg.append("用户名已存在；");
            }
        }

        String nickname = userImportDTO.getNickname();
        if (StrUtil.isBlank(nickname)) {
            validationMsg.append("用户昵称为空；");
        }

        String mobile = userImportDTO.getMobile();
        if (StrUtil.isBlank(mobile)) {
            validationMsg.append("手机号码为空；");
        } else {
            if (!Validator.isMobile(mobile)) {
                validationMsg.append("手机号码不正确；");
            }
        }

        if (validationMsg.isEmpty()) {
            // 校验通过，持久化至数据库
            User entity = userConverter.toEntity(userImportDTO);
            entity.setPassword(passwordEncoder.encode(SystemConstants.DEFAULT_PASSWORD));   // 默认密码
            // 性别翻译
            String genderLabel = userImportDTO.getGenderLabel();
            if (StrUtil.isNotBlank(genderLabel)) {
                Integer genderValue = (Integer) IBaseEnum.getValueByLabel(genderLabel, GenderEnum.class);
                entity.setGender(genderValue);
            }

            // 角色解析
            String roleCodes = userImportDTO.getRoleCodes();
            List<Long> roleIds = null;
            if (StrUtil.isNotBlank(roleCodes)) {
                roleIds = roleService.list(
                                new LambdaQueryWrapper<Role>()
                                        .in(Role::getCode, (Object) roleCodes.split(","))
                                        .eq(Role::getStatus, StatusEnum.ENABLE.getValue())
                                        .select(Role::getId)
                        ).stream()
                        .map(Role::getId)
                        .collect(Collectors.toList());
            }
            // 部门解析
            String deptCode = userImportDTO.getDeptCode();
            if (StrUtil.isNotBlank(deptCode)) {
                Dept dept = deptService.getOne(new LambdaQueryWrapper<Dept>().eq(Dept::getCode, deptCode)
                        .select(Dept::getId)
                );
                if (dept != null) {
                    entity.setDeptId(dept.getId());
                }
            }


            boolean saveResult = userService.save(entity);
            if (saveResult) {
                validCount++;
                // 保存用户角色关联
                if (CollectionUtil.isNotEmpty(roleIds)) {
                    List<UserRole> userRoles = roleIds.stream()
                            .map(roleId -> new UserRole(entity.getId(), roleId))
                            .collect(Collectors.toList());
                    userRoleService.saveBatch(userRoles);
                }
            } else {
                invalidCount++;
                msg.append("第").append(validCount + invalidCount).append("行数据保存失败；<br/>");
            }
        } else {
            invalidCount++;
            msg.append("第").append(validCount + invalidCount).append("行数据校验失败：").append(validationMsg + "<br/>");
        }
    }


    /**
     * 所有数据解析完成会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("所有数据解析完成！");

    }


    @Override
    public String getMsg() {
        // 总结信息
        return StrUtil.format("导入用户结束：成功{}条，失败{}条；<br/>{}", validCount, invalidCount, msg);
    }
}
