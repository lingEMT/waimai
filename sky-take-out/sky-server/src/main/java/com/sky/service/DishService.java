package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    void saveWithFlavors(DishDTO dishDTO);

    DishVO getById(Long id);

    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    void enableOrDisable(Integer status, Long id);

    void updateWithFlavors(DishDTO dishDTO);

    void deleteBatch(List<Long> ids);

    List<DishVO> listWithFlavor(Dish dish);

    List<Dish> list(Long categoryId);
}
