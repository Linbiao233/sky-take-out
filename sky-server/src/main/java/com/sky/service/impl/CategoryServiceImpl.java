package com.sky.service.impl;

import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.entity.Category;
import com.sky.entity.Employee;
import com.sky.mapper.CategoryMapper;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 新增套餐分类
     *
     * @param categoryDTO
     */
    public void save(CategoryDTO categoryDTO) {
        //使用的时候建议使用实体类，下面进行转换
        Category category = new Category();
        //将Dto中的值copy到employee中
        BeanUtils.copyProperties(categoryDTO, category);
        //上述只是将相同的属性的值copy进去了，部分不相同的没有进去所以需要手动set
        //状态，具有相应的code码来替代1，0，默认为1
        category.setStatus(StatusConstant.ENABLE);
        //创建时间，修改时间
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        //设置当前记录的创建人id和修改人id，调用的是存入当前线程的id
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.insert(category);
    }
}
