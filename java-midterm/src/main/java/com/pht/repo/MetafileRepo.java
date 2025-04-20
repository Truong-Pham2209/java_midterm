package com.pht.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pht.entity.MetafileEntity;

@Repository
public interface MetafileRepo extends JpaRepository<MetafileEntity, UUID>{

}
