package com.example.crud_mysql.repository;

import com.example.crud_mysql.model.Enum.LifeCycle;
import com.example.crud_mysql.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findAllByLifeCycle(Pageable pageable, LifeCycle lifeCycle);
}
