package com.storyteller.platform.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.storyteller.platform.models.Sales;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long> {
}