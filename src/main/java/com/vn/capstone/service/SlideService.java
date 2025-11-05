package com.vn.capstone.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.vn.capstone.domain.Product;
import com.vn.capstone.domain.Slide;
import com.vn.capstone.domain.User;
import com.vn.capstone.domain.response.ResUserDTO;
import com.vn.capstone.domain.response.ResultPaginationDTO;
import com.vn.capstone.domain.response.slide.ResSlideDTO;
import com.vn.capstone.repository.SlideRepository;
import com.vn.capstone.util.constant.SlideType;

import jakarta.transaction.Transactional;

@Service
public class SlideService {

    private final SlideRepository slideRepository;

    public SlideService(SlideRepository slideRepository) {
        this.slideRepository = slideRepository;
    }

    public ResultPaginationDTO fetchAllSlide(Specification<Slide> spec, Pageable pageable) {
        Page<Slide> pageSlide = this.slideRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageSlide.getTotalPages());
        mt.setTotal(pageSlide.getTotalElements());

        rs.setMeta(mt);

        // Chỉ dùng DTO, không set raw entity
        List<ResSlideDTO> listSlide = pageSlide.getContent()
                .stream()
                .map(this::convertToResSlideDTO)
                .collect(Collectors.toList());

        rs.setResult(listSlide); // CHỈ set DTO
        return rs;
    }

    public ResSlideDTO convertToResSlideDTO(Slide slide) {
        ResSlideDTO res = new ResSlideDTO();
        res.setId(slide.getId());
        res.setTitle(slide.getTitle());
        res.setDescription(slide.getDescription());
        res.setImageUrl(slide.getImageUrl());
        res.setUpdatedAt(slide.getUpdatedAt());
        res.setCreatedAt(slide.getCreatedAt());
        res.setRedirectUrl(slide.getRedirectUrl());
        res.setActive(slide.getActive());
        res.setType(slide.getType());
        res.setOrderIndex(slide.getOrderIndex());
        return res;
    }

    public List<Slide> getSlidesByType(SlideType type) {
        return slideRepository.findByType(type);
    }

    public Slide createSlide(Slide slide) {
        return slideRepository.save(slide);
    }

    public Slide fetchSlideById(long id) {
        Optional<Slide> SlideOptional = this.slideRepository.findById(id);
        if (SlideOptional.isPresent()) {
            return SlideOptional.get();
        }
        return null;
    }

    @Transactional
    public Slide handleUpdateSlide(Slide reqSlide) {
        Slide currentSlide = this.fetchSlideById(reqSlide.getId());
        if (currentSlide != null) {
            currentSlide.setTitle(reqSlide.getTitle());
            currentSlide.setDescription(reqSlide.getDescription());
            currentSlide.setImageUrl(reqSlide.getImageUrl());
            currentSlide.setRedirectUrl(reqSlide.getRedirectUrl());
            currentSlide.setActive(reqSlide.getActive());
            currentSlide.setOrderIndex(reqSlide.getOrderIndex());
            currentSlide.setType(reqSlide.getType());
            // update
            currentSlide = this.slideRepository.save(currentSlide);
        }
        return currentSlide;
    }

    public void deleteSlide(Long id) {
        slideRepository.deleteById(id);
    }

}
