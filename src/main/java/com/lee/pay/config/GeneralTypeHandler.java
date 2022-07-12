package com.lee.pay.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;

@MappedTypes(value = {JSONObject.class, Date.class})
@MappedJdbcTypes(value = {JdbcType.VARCHAR,JdbcType.BLOB} , includeNullJdbcType = true)
public class GeneralTypeHandler<T> extends BaseTypeHandler<T> {

    private final Class<T> clazz;

    public GeneralTypeHandler(Class<T> clazz){
        if (clazz == null) throw new IllegalArgumentException("Argument cannot be null !");
        this.clazz = clazz;
    }

    /**
     * 设置非空参数
     */
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement,
                                    int i, T parameter, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, JSON.toJSONString(parameter));
    }

    /**
     * 根据列索引，获取可以为空的结果
     */
    @Override
    public T getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        String sqlJson = resultSet.getString(columnName);
        if (null != sqlJson){
            return JSONObject.parseObject(sqlJson , clazz);
        }
        return null;
    }

    /**
     * 根据列索引，获取可以为空的结果
     */
    @Override
    public T getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        String sqlJson = resultSet.getString(columnIndex);
        if (null != sqlJson){
            return JSONObject.parseObject(sqlJson , clazz);
        }
        return null;
    }

    /**
     * 根据列索引，获取可以为空的结果
     */
    @Override
    public T getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        String sqlJson = callableStatement.getString(columnIndex);
        if (null != sqlJson){
            return JSONObject.parseObject(sqlJson , clazz);
        }
        return null;
    }
}
