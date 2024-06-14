package com.robot.transform.demo.bean;

import com.robot.transform.annotation.TransformDict;
import com.robot.transform.annotation.TransformEnum;
import com.robot.transform.demo.controller.transform.TransformClass;
import com.robot.transform.demo.enums.Sex;
import lombok.Data;

import java.util.List;

/**
 * 学生VO
 *
 * @author R
 * @since 2022-08
 */
@Data
@SuppressWarnings("all")
public class StudentVO {

    private Long id;

    // 名字
    private String name;

    // 性别-枚举（0-男，1-女）
    @TransformEnum
    private Sex sex;

    // 性别名称（枚举转换）
    private String sexName;

    // 爱好code
    @TransformDict(group = "hobby")
    private Integer hobby;

    // 爱好名称（数据字典转换）
    private String hobbyName;

    // 班级id
    @TransformClass("className")
    private Long classId;

    // 班级名称（自定义转换：根据班级id查询name）
    private String className;

    // 小组成员（嵌套转换）
    private List<StudentVO> team;

    // 同桌（嵌套转换）
    private StudentVO deskmate;

    private String email;

}
