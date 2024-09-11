package com.youlai.boot.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.boot.common.model.Option;
import com.youlai.boot.system.model.entity.Dict;
import com.youlai.boot.system.model.form.DictForm;
import com.youlai.boot.system.model.query.DictPageQuery;
import com.youlai.boot.system.model.vo.DictPageVO;

import java.util.List;

/**
 * 数据字典业务接口
 *
 * @author haoxr
 * @since 2022/10/12
 */
public interface DictService extends IService<Dict> {

    /**
     * 字典分页列表
     *
     * @param queryParams 分页查询对象
     * @return
     */
    Page<DictPageVO> getDictPage(DictPageQuery queryParams);


    /**
     * 获取字典表单详情
     *
     * @param id 字典ID
     * @return
     */
    DictForm getDictForm(Long id);


    /**
     * 新增字典
     *
     * @param dictForm 字典表单
     * @return
     */
    boolean saveDict(DictForm dictForm);


    /**
     * 修改字典
     *
     * @param id
     * @param dictForm 字典表单
     * @return
     */
    boolean updateDict(Long id, DictForm dictForm);

    /**
     * 删除字典
     *
     * @param idsStr 字典ID，多个以英文逗号(,)分割
     * @return
     */
    void deleteDictByIds(String idsStr);


    /**
     * 获取字典的数据项
     *
     * @param code 字典编码
     * @return
     */
    List<Option<Long>> listDictItemsByCode(String code);


    /**
     * 获取字典列表
     *
     * @return
     */
    List<Option<String>> getDictList();
}
