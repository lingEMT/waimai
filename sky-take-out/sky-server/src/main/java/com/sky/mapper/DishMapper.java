package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

     /**
      * 新增菜品
      * @param dish
      */
     @Options(useGeneratedKeys = true, keyProperty = "id")
     @AutoFill(value = OperationType.INSERT)
     @Insert("insert into dish (name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) " +
             "values (#{name}, #{categoryId}, #{price}, #{image}, #{description}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
     void insertDish(Dish dish);

     /**
      * 新增菜品口味
      * @param flavors
      */
     void insertDishFlavors(List<DishFlavor> flavors);

     /**
      * 根据id查询菜品
      * @param id
      * @return
      */
     @Select("select * from dish where id = #{id}")
     Dish getById(Long id);

     /**
      * 根据菜品id查询菜品口味
      * @param dishId
      * @return
      */
     @Select("select * from dish_flavor where dish_id = #{dishId}")
     List<DishFlavor> getFlavorsByDishId(Long dishId);

     /**
      * 分页查询菜品
      * @param dishPageQueryDTO
      * @return
      */
     Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

     /**
      * 更新菜品
      * @param dish
      */
     @AutoFill(value = OperationType.UPDATE)
     void update(Dish dish);

      /**
       * 删除菜品口味
       * @param dishIds
       */
     void deleteDishFlavorsBatch(List<Long> dishIds);

      /**
       * 批量删除菜品
       * @param idList
       */
     void deleteBatch(List<Long> idList);

     /**
      * 根据套餐id查询关联的套餐数量
      * @param dishId
      * @return
      */
     @Select("select count(id) from setmeal_dish where dish_id = #{dishId}")
     Integer countSetmealIdByDishId(Long dishId);
}
