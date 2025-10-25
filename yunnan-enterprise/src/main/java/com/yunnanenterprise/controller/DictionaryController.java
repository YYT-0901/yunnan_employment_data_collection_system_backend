package com.yunnanenterprise.controller;

import com.yunnancommon.controller.ABaseController;
import com.yunnancommon.entity.vo.ResponseVO;
import com.yunnanenterprise.dictionary.DictionaryItem;
import com.yunnanenterprise.dictionary.DictionaryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 字典接口，给前端下拉用
 */
@RestController
@RequestMapping("/api/dictionaries")
public class DictionaryController extends ABaseController {

    private final DictionaryService dict;

    public DictionaryController(DictionaryService dict) {
        this.dict = dict;
    }

    @GetMapping("/employment-reduction-types")
    public ResponseVO<List<DictionaryItem>> types() {
        return getSuccessResponseVO(dict.getReductionTypes());
    }

    @GetMapping("/employment-reduction-causes")
    public ResponseVO<List<DictionaryItem>> causes() {
        return getSuccessResponseVO(dict.getReductionCauses());
    }
}