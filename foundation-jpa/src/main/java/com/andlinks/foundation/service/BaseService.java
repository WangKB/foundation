package com.andlinks.foundation.service;

import com.andlinks.foundation.entity.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

/**
 * Created by 王凯斌 on 2017/4/24.
 */
public interface BaseService<T extends BaseEntity> {

    T find(Long id);

    List<T> findAll();

    List<T> findList(Long[] ids);

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

    void delete(Long[] ids);

}
