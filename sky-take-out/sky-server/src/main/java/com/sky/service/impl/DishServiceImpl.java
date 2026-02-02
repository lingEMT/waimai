package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @Override
    @Transactional
    public void saveWithFlavors(DishDTO dishDTO) {
        //新增菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setStatus(0);
        dishMapper.insertDish(dish); //设置主键回显
        // 新增菜品口味前添加空检查
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(df -> df.setDishId(dish.getId()));
            // 新增菜品口味
            dishMapper.insertDishFlavors(flavors);
        }
    }

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    @Override
    public DishVO getById(Long id) {
        Dish dish = dishMapper.getById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        // 查询菜品口味
        List<DishFlavor> flavors = dishMapper.getFlavorsByDishId(id);
        if (flavors != null && !flavors.isEmpty()) {
            dishVO.setFlavors(flavors);
        }
        return dishVO;
    }

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

     /**
      * 修改菜品状态
      * @param status
      * @param id
      */
    @Override
    @Transactional
    public void enableOrDisable(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.update(dish);
    }

     /**
      * 修改菜品
      * @param dishDTO
      */
    @Override
    @Transactional
    public void updateWithFlavors(DishDTO dishDTO) {
        // 更新菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        // 更新菜品口味前添加空检查
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(df -> df.setDishId(dish.getId())); // 设置菜品id
        }
        // 删除原有的口味
        dishMapper.deleteDishFlavorsBatch(Collections.singletonList(dish.getId()));
        // 新增更新后的口味
        dishMapper.insertDishFlavors(flavors);
    }

     /**
      * 删除菜品
      * @param ids 逗号分隔的菜品id字符串
      */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        ids.stream()
                .forEach(id -> {
                    Dish dish = dishMapper.getById(id);
                    // 检查菜品是否为起售状态
                    if(dish.getStatus() == 1){
                        throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
                    }
                    // 检查菜品是否关联了套餐
                    Integer count = dishMapper.countSetmealIdByDishId(id);
                    if (count > 0) {
                        throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
                    }
                });

        // 调用Mapper方法删除菜品
        dishMapper.deleteBatch(ids);
        // 删除菜品口味
        dishMapper.deleteDishFlavorsBatch(ids);
    }
}