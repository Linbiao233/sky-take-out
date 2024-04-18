package com.sky.service;

import com.sky.dto.CategoryDTO;

public interface CategoryService {

    /**
     * 新增套餐分类
     *
     * @param categoryDTO
     */
    void save(CategoryDTO categoryDTO);
}
