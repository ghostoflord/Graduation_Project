package com.vn.capstone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.vn.capstone.domain.Slide;
import com.vn.capstone.util.constant.SlideType;

@Repository
public interface SlideRepository extends JpaRepository<Slide, Long>, JpaSpecificationExecutor<Slide> {
    List<Slide> findAllByActiveTrueAndTypeOrderByOrderIndexAsc(String type);

    List<Slide> findByType(SlideType type);
}
