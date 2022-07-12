package com.lee.pay.utils.crud.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface RawSqlMapper {

    List<Map<String, Object>> rawSelect(String sql);

    Integer rawCount(String sql);

    Integer rawUpdate(String updateSql);

    void rawInsert(String insertSql);

    int rawDelete(String insertSql);
}
