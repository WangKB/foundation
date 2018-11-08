package com.andlinks.foundation.service;

import com.andlinks.foundation.entity.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Set;

/**
 * Created by 王凯斌 on 2017/4/24.
 */
public interface BaseService<T extends BaseEntity> {

    T find(Long id);

    T find(Condition... conditions);

    List<T> findAll();

    List<T> sortList(Sort sort, Condition... conditions);

    List<T> findList(Long[] ids);

    List<T> findList(Condition... conditions);

    Set<T> findSet(Long[] ids);

    Boolean exists(Long id);

    T save(T t);

    T update(T t);

    T update(T t, String... ignore);

    @SuppressWarnings("unchecked")
    void delete(T... ts);

    void delete(T t);

    void delete(Long id);

    Page<T> findPage(Pageable pageable);

    Page<T> findPage(Pageable pageable, Condition... conditions);

    void delete(Long[] ids);

    Long count();

    Long count(Condition... conditions);

    <AT> List<AT> getAttributesList(String attributeName);

    <AT> List<AT> getAttributesList(String attributeName, Condition... conditions);

}
