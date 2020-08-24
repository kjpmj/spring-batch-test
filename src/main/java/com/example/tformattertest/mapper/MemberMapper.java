package com.example.tformattertest.mapper;

import com.example.tformattertest.annotation.TncMyBatisMapper;
import com.example.tformattertest.vo.Member;

import java.util.List;

@TncMyBatisMapper
public interface MemberMapper {

    public List<Member> getMembers();
}
