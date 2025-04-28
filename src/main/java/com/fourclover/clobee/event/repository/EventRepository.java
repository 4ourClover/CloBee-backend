package com.fourclover.clobee.event.repository;

import com.fourclover.clobee.event.domain.EventInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EventRepository {
    List<EventInfo> getTotalAttend();
}
