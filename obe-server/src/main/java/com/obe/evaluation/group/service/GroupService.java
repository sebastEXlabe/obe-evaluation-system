package com.obe.evaluation.group.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.obe.evaluation.group.entity.*;
import com.obe.evaluation.group.mapper.*;
import com.obe.evaluation.system.mapper.CourseMapper;
import com.obe.evaluation.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service @RequiredArgsConstructor
public class GroupService extends ServiceImpl<ProjectGroupMapper, ProjectGroup> {
    private final GroupMemberMapper memberMapper;
    private final SysUserMapper userMapper;
    private final CourseMapper courseMapper;

    @Override @Transactional
    public boolean save(ProjectGroup group) {
        if (group.getMaxMembers() == null || group.getMaxMembers() <= 0) group.setMaxMembers(8);
        if (group.getInviteCode() == null || group.getInviteCode().isEmpty()) {
            String code; int tries = 0;
            do { code = generateCode(); tries++; }
            while (tries < 20 && getOne(new LambdaQueryWrapper<ProjectGroup>().eq(ProjectGroup::getInviteCode, code)) != null);
            group.setInviteCode(code);
        }
        return super.save(group);
    }

    private String generateCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) sb.append(chars.charAt(ThreadLocalRandom.current().nextInt(chars.length())));
        return sb.toString();
    }

    public Map<String, Object> getGroupDetail(Long groupId) {
        ProjectGroup group = getById(groupId);
        if (group == null) return new LinkedHashMap<>();
        List<GroupMember> members = memberMapper.selectList(new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getGroupId, groupId));
        List<Map<String, Object>> enriched = new ArrayList<>();
        for (GroupMember m : members) {
            var user = userMapper.selectById(m.getUserId());
            enriched.add(Map.of("id", m.getId(), "userId", m.getUserId(), "roleCode", m.getRoleCode(),
                "realName", user != null ? user.getRealName() : "用户" + m.getUserId()));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("group", group); result.put("members", enriched); result.put("memberCount", members.size());
        if (group.getCourseId() != null) {
            var course = courseMapper.selectById(group.getCourseId());
            result.put("courseName", course != null ? course.getCourseName() : "未分配");
            result.put("semester", course != null ? course.getSemester() : "");
        }
        return result;
    }

    @Transactional
    public GroupMember joinGroup(Long groupId, Long userId, String roleCode) {
        if (memberMapper.selectCount(new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getGroupId, groupId).eq(GroupMember::getUserId, userId)) > 0)
            throw new IllegalArgumentException("你已在该小组中");
        ProjectGroup group = getById(groupId);
        if (group == null) throw new IllegalArgumentException("小组不存在");
        if (memberMapper.selectCount(new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getGroupId, groupId)) >= group.getMaxMembers())
            throw new IllegalArgumentException("小组已满");
        GroupMember member = new GroupMember();
        member.setGroupId(groupId); member.setUserId(userId); member.setRoleCode(roleCode);
        memberMapper.insert(member); return member;
    }

    public void removeMember(Long memberId) { memberMapper.deleteById(memberId); }
    public GroupMemberMapper getMemberMapper() { return memberMapper; }

    @Transactional
    public boolean removeGroupCascade(Long groupId) {
        memberMapper.delete(new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getGroupId, groupId));
        return super.removeById(groupId);
    }
}
