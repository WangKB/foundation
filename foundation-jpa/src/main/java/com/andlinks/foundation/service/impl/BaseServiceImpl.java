package com.andlinks.foundation.service.impl;

import com.andlinks.foundation.dao.BaseDao;
import com.andlinks.foundation.entity.BaseEntity;
import com.andlinks.foundation.service.BaseService;
import com.andlinks.foundation.service.Condition;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by 王凯斌 on 2017/4/24.
 */
public class BaseServiceImpl<T extends BaseEntity> implements BaseService<T> {

    private final Logger logger = LoggerFactory.getLogger(BaseServiceImpl.class);
    /**
     * 更新忽略属性
     */
    private static final String[] UPDATE_IGNORE_PROPERTIES = new String[]{
            BaseEntity.CREATE_DATE_PROPERTY_NAME,
            BaseEntity.MODIFY_DATE_PROPERTY_NAME,
            BaseEntity.VERSION_PROPERTY_NAME,
            BaseEntity.DELETED_PROPERTY_NAME};

    @Autowired
    private BaseDao<T> baseDao;

    @Override
    public T find(Long id) {

        if (id == null) {
            return null;
        }
        return baseDao.findOne(id);
    }

    @Override
    public List<T> findAll() {

        List<T> result = new ArrayList<T>();
        baseDao.findAll().forEach(result::add);
        return result;
    }

    @Override
    public List<T> findList(Long[] ids) {

        List<T> result = new ArrayList<T>();
        if (ids == null) {
            return result;
        }
        ids = Arrays.stream(ids)
                .filter(s -> (s != null))
                .toArray(Long[]::new);
        baseDao.findAll(Arrays.asList(ids)).forEach(result::add);
        return result;
    }

    @Override
    public Set<T> findSet(Long[] ids) {

        return new HashSet<T>(findList(ids));
    }

    @Override
    public Boolean exists(Long id) {
        if (id == null) {
            return false;
        }
        return baseDao.exists(id);
    }

    @Override
    public T save(T t) {

        if (!t.isNew()) {
            throw new IllegalArgumentException("id should be empty");
        }
        return baseDao.save(t);
    }

    @Override
    public T update(T t) {

        return update(t, UPDATE_IGNORE_PROPERTIES);
    }

    @Override
    public T update(T t, String... ignore) {

        if (!baseDao.exists(t.getId())) {
            throw new IllegalArgumentException("update target does not exits");
        }
        T orginal = baseDao.findOne(t.getId());
        if (orginal != null) {
            copyProperties(t, orginal, (String[]) ArrayUtils.addAll(ignore, UPDATE_IGNORE_PROPERTIES));
        }
        return baseDao.save(orginal);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(T... ts) {

        for (T t : ts) {
            delete(t);
        }
    }

    @Override
    public void delete(T t) {

        if (t == null || !baseDao.exists(t.getId())) {
            throw new IllegalArgumentException("delete target does not exits");
        }
        t.setDeleted(true);
        baseDao.save(t);
    }

    @Override
    public void delete(Long id) {

        delete(find(id));
    }

    protected void copyProperties(T source, T target,
                                  String... ignoreProperties) {

        PropertyDescriptor[] propertyDescriptors = PropertyUtils
                .getPropertyDescriptors(target);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String propertyName = propertyDescriptor.getName();
            Method readMethod = propertyDescriptor.getReadMethod();
            Method writeMethod = propertyDescriptor.getWriteMethod();
            if (ArrayUtils.contains(ignoreProperties, propertyName)
                    || readMethod == null || writeMethod == null) {
                continue;
            }
            try {
                Object sourceValue = readMethod.invoke(source);
                writeMethod.invoke(target, sourceValue);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage(), e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e.getMessage(), e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    @Override
    public Page<T> findPage(Pageable pageable) {

        return baseDao.findAll(pageable);
    }

    @Override
    public Page<T> findPage(Pageable pageable, Condition... conditions) {

        return baseDao.findAll(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                return generate(root, criteriaBuilder, conditions);
            }
        }, pageable);
    }

    private Predicate generate(Root<T> root, CriteriaBuilder criteriaBuilder, Condition... conditions) {
        Predicate predicate = criteriaBuilder.conjunction();
        if (conditions == null) {
            return predicate;
        }
        for (Condition condition : conditions) {

            String attribute = condition.getAttribute();
            Object value = condition.getValue();
            Comparable comparableValue = condition.getComparableValue();

            switch (condition.getType()) {
                case EQUAL:
                    predicate.getExpressions().add(
                            criteriaBuilder.equal(root.get(attribute), value));
                    break;
                case UNEQUAL:
                    predicate.getExpressions().add(
                            criteriaBuilder.notEqual(root.get(attribute), value));
                    break;
                case GREATER:
                    predicate.getExpressions().add(
                            criteriaBuilder.greaterThan(root.get(attribute), comparableValue));
                    break;
                case GE:
                    predicate.getExpressions().add(
                            criteriaBuilder.greaterThanOrEqualTo(root.get(attribute), comparableValue));
                    break;
                case LESS:
                    predicate.getExpressions().add(
                            criteriaBuilder.lessThan(root.get(attribute), comparableValue));
                    break;
                case LE:
                    predicate.getExpressions().add(
                            criteriaBuilder.lessThanOrEqualTo(root.get(attribute), comparableValue));
                    break;
                case LIKE:
                    predicate.getExpressions().add(
                            criteriaBuilder.like(root.get(attribute), "%" + value + "%"));
                    break;
                case SEARCH:
                    String[] searchAttributes = condition.getSearchAttributes();
                    Predicate[] predicates = new Predicate[searchAttributes.length];

                    for (int i = 0; i < predicates.length; i++) {
                        predicates[i] = criteriaBuilder.like(root.get(searchAttributes[i]), "%" + value + "%");
                    }
                    predicate.getExpressions().add(criteriaBuilder.or(predicates));
                    break;
            }
        }
        return predicate;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long[] ids) {

        for (Long id : ids) {
            delete(id);
        }
    }

    @Override
    public List<T> findList(Condition... conditions) {
        return baseDao.findAll(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return generate(root, criteriaBuilder, conditions);
            }
        });
    }

    @Override
    public Long count() {
        return baseDao.count();
    }

    @Override
    public Long count(Condition... conditions) {
        return baseDao.count(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return generate(root, criteriaBuilder, conditions);
            }
        });
    }

    @Override
    public <AT> List<AT> getAttributesList(String attributeName) {

        List<T> list = new ArrayList<>();
        baseDao.findAll().forEach(list::add);
        return getResult(attributeName, list);
    }

    @Override
    public <AT> List<AT> getAttributesList(String attributeName, Condition... conditions) {
        List<T> list = new ArrayList<>();
        baseDao.findAll(new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return generate(root, criteriaBuilder, conditions);
            }
        }).forEach(list::add);
        return getResult(attributeName, list);
    }

    private <AT> List<AT> getResult(String attributeName, List<T> list) {

        List<AT> result = new ArrayList<>();
        try {
            for (T t : list) {
                Field field = t.getClass().getDeclaredField(attributeName);
                result.add((AT) field.get(t));
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.error("服务器内部错误，请联系管理员", e);
        }

        return result;
    }
}
