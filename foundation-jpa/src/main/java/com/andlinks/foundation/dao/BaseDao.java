package com.andlinks.foundation.dao;

import com.andlinks.foundation.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 继承了PagingAndSortingRepository，无需再手动实现数据库交互
 * Created by 王凯斌 on 2017/4/24.
 */
public interface BaseDao<T extends BaseEntity> extends PagingAndSortingRepository<T, Long>, JpaSpecificationExecutor<T> {

}
