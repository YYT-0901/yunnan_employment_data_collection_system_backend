package com.yunnancommon.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
/**
 * @Description:账号信息表Mapper
 * @auther:group2
 * @date:2025/10/22
 */

@Mapper
public interface AccountInfoMapper<T, P> extends BaseMapper {
	/**
	 * 根据Username查询
	 */
	T selectByUsername(@Param("username") String username);

	/**
	 * 根据Username更新
	 */
	Integer updateByUsername(@Param("bean") T t, @Param("username") String username);

	/**
	 * 根据Username删除
	 */
	Integer deleteByUsername(@Param("username") String username);


}