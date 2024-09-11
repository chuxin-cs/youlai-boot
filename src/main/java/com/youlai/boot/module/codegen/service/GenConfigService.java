package com.youlai.boot.module.codegen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.boot.module.codegen.model.entity.GenConfig;
import com.youlai.boot.module.codegen.model.form.GenConfigForm;

/**
 * 代码生成配置接口
 *
 * @author Ray
 * @since 2.10.0
 */
public interface GenConfigService extends IService<GenConfig> {

    /**
     * 获取代码生成配置
     *
     * @param tableName 表名
     * @return
     */
    GenConfigForm getGenConfigFormData(String tableName);

    /**
     * 保存代码生成配置
     *
     * @param formData 表单数据
     * @return
     */
    void saveGenConfig(GenConfigForm formData);

    /**
     * 删除代码生成配置
     *
     * @param tableName 表名
     * @return
     */
    void deleteGenConfig(String tableName);

}
