package com.ganshuai.darkhorse.repository;

import com.ganshuai.darkhorse.repository.entity.FlightOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightOrderRepository extends JpaRepository<FlightOrderEntity, Long> {
}
