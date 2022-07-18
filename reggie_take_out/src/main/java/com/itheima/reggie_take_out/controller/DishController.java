package com.itheima.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie_take_out.common.Result;
import com.itheima.reggie_take_out.dto.DishDto;
import com.itheima.reggie_take_out.entity.Category;
import com.itheima.reggie_take_out.entity.Dish;
import com.itheima.reggie_take_out.entity.DishFlavor;
import com.itheima.reggie_take_out.service.CategoryService;
import com.itheima.reggie_take_out.service.DishFlavorService;
import com.itheima.reggie_take_out.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        return Result.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name) {
        // 构造分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        // 页面需要展示菜品名称，而dish对象没有菜品名称，只能构造DishDto对象
        Page<DishDto> dishDtoPage = new Page<>();
        // TODO 此方法需要优化

        // 条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        // 添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);

        // 添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        // 执行分页查询
        dishService.page(pageInfo, queryWrapper);

        // 对象拷贝，页面除了数据之外的信息全部拷贝，例如总数，每页多少条
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        // DishDto对象赋值，也就是组装页面数据信息
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> dishDtoList = records.stream().map((item) -> {
            DishDto dishDto  = new DishDto();
            // 给dto对象赋值
            BeanUtils.copyProperties(item, dishDto);
            // 拿到菜品分类id
            Long categoryId = item.getCategoryId();
            // 根据菜品分类id查询菜品分类对象，给dto对象赋值菜品名称
            Category category = categoryService.getById(categoryId);
            if(category != null) {
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dishDtoList);
        return Result.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return Result.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);

        return Result.success("修改菜品成功");
    }

//    /**
//     * 根据条件查询对应的菜品数据
//     * @param dish
//     * @return
//     */
//    @GetMapping("/list")
//    public Result<List<Dish>> list(Dish dish) {
//        // 构造查询条件
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
//        // 添加条件，查询状态为1（起售状态）的菜品
//        queryWrapper.eq(Dish::getStatus, 1);
//
//        // 添加排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return Result.success(list);
//    }

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public Result<List<DishDto>> list(Dish dish) {
        // 构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        // 添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus, 1);

        // 添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto  = new DishDto();
            // 给dto对象赋值
            BeanUtils.copyProperties(item, dishDto);
            // 拿到菜品分类id
            Long categoryId = item.getCategoryId();
            // 根据菜品分类id查询菜品分类对象，给dto对象赋值菜品名称
            Category category = categoryService.getById(categoryId);
            if(category != null) {
                dishDto.setCategoryName(category.getName());
            }
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            // SQL: select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        return Result.success(dishDtoList);
    }
}
