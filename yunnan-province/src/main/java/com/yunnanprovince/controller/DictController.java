package com.yunnanprovince.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.vo.ResponseVO;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
@RequestMapping("/dict")
public class DictController extends ABaseController {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/returnReasons")
    public ResponseVO getReturnReasons() {
        try {
            ClassPathResource res = new ClassPathResource("dict/return_reason_category.json");
            try (InputStream is = res.getInputStream()) {
                JsonNode node = objectMapper.readTree(is);
                return getSuccessResponseVO(node);
            }
        } catch (Exception e) {
            return getErrorResponseVO("加载退回原因分类失败");
        }
    }

    @GetMapping("/returnFields")
    public ResponseVO getReturnFields() {
        try {
            ClassPathResource res = new ClassPathResource("dict/return_problem_fields.json");
            try (InputStream is = res.getInputStream()) {
                JsonNode node = objectMapper.readTree(is);
                return getSuccessResponseVO(node);
            }
        } catch (Exception e) {
            return getErrorResponseVO("加载退回问题字段失败");
        }
    }
}
