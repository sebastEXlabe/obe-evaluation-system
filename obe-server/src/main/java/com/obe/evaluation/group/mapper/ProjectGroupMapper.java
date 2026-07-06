package com.obe.evaluation.group.mapper;

import com.obe.evaluation.group.entity.ProjectGroup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProjectGroupMapper extends BaseMapper<ProjectGroup> {
    /** 绕过@TableLogic查询（包括已删除记录） */
    @Select("SELECT * FROM project_group WHERE id = #{id}")
    ProjectGroup findByIdIncludeDeleted(Long id);
}
