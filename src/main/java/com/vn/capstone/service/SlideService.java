package com.vn.capstone.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vn.capstone.domain.Slide;
import com.vn.capstone.repository.SlideRepository;
import com.vn.capstone.util.constant.SlideType;

@Service
public class SlideService {

    private final SlideRepository slideRepository;

    public SlideService(SlideRepository slideRepository) {
        this.slideRepository = slideRepository;
    }

    public List<Slide> getAllSlides() {
        return slideRepository.findAll();
    }

    public List<Slide> getSlidesByType(SlideType type) {
        return slideRepository.findByType(type);
    }

    public Slide createSlide(Slide slide) {
        return slideRepository.save(slide);
    }

    public Slide updateSlide(Long id, Slide slide) {
        return slideRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(slide.getTitle());
                    existing.setDescription(slide.getDescription());
                    existing.setImageUrl(slide.getImageUrl());
                    existing.setRedirectUrl(slide.getRedirectUrl());
                    existing.setActive(slide.getActive());
                    existing.setOrderIndex(slide.getOrderIndex());
                    existing.setType(slide.getType());
                    return slideRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Slide not found with id " + id));
    }

    public void deleteSlide(Long id) {
        slideRepository.deleteById(id);
    }

}
