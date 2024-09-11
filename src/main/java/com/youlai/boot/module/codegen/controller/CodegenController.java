package com.youlai.boot.module.codegen.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.common.result.PageResult;
import com.youlai.boot.common.result.Result;
import com.youlai.boot.config.property.CodegenProperties;
import com.youlai.boot.common.enums.LogModuleEnum;
import com.youlai.boot.module.codegen.service.CodegenService;
import com.youlai.boot.module.codegen.model.form.GenConfigForm;
import com.youlai.boot.module.codegen.model.query.TablePageQuery;
import com.youlai.boot.module.codegen.model.vo.CodegenPreviewVO;
import com.youlai.boot.module.codegen.model.vo.TablePageVO;
import com.youlai.boot.common.annotation.Log;
import com.youlai.boot.module.codegen.service.GenConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 代码生成器控制层
 *
 * @author Ray
 * @since 2.10.0
 */
@Tag(name = "09.代码生成")
@RestController
@RequestMapping("/api/v1/codegen")
@RequiredArgsConstructor
@Slf4j
public class CodegenController {

    private final CodegenService codegenService;
    private final GenConfigService genConfigService;
    private final CodegenProperties codegenProperties;

    @Operation(summary = "获取数据表分页列表")
    @GetMapping("/table/page")
    @Log(value = "代码生成分页列表", module = LogModuleEnum.OTHER)
    public PageResult<TablePageVO> getTablePage(
            TablePageQuery queryParams
    ) {
        Page<TablePageVO> result = codegenService.getTablePage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "获取代码生成配置")
    @GetMapping("/{tableName}/config")
    public Result<GenConfigForm> getGenConfigFormData(
            @Parameter(description = "表名", example = "sys_user") @PathVariable String tableName
    ) {
        GenConfigForm formData = genConfigService.getGenConfigFormData(tableName);
        return Result.success(formData);
    }

    @Operation(summary = "保存代码生成配置")
    @PostMapping("/{tableName}/config")
    @Log(value = "生成代码", module = LogModuleEnum.OTHER)
    public Result<?> saveGenConfig(@RequestBody GenConfigForm formData) {
        genConfigService.saveGenConfig(formData);
        return Result.success();
    }

    @Operation(summary = "删除代码生成配置")
    @DeleteMapping("/{tableName}/config")
    public Result<?> deleteGenConfig(
            @Parameter(description = "表名", example = "sys_user") @PathVariable String tableName
    ) {
        genConfigService.deleteGenConfig(tableName);
        return Result.success();
    }

    @Operation(summary = "获取预览生成代码")
    @GetMapping("/{tableName}/preview")
    @Log(value = "预览生成代码", module = LogModuleEnum.OTHER)
    public Result<List<CodegenPreviewVO>> getTablePreviewData(@PathVariable String tableName) {
        List<CodegenPreviewVO> list = codegenService.getCodegenPreviewData(tableName);
        return Result.success(list);
    }

    @Operation(summary = "下载代码")
    @GetMapping("/{tableName}/download")
    @Log(value = "下载代码", module = LogModuleEnum.OTHER)
    public void downloadZip(HttpServletResponse response, @PathVariable String tableName) {
        String[] tableNames = tableName.split(",");
        byte[] data = codegenService.downloadCode(tableNames);

        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(codegenProperties.getDownloadFileName(), StandardCharsets.UTF_8));
        response.setContentType("application/octet-stream; charset=UTF-8");

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            log.error("Error while writing the zip file to response", e);
            throw new RuntimeException("Failed to write the zip file to response", e);
        }
    }
}
