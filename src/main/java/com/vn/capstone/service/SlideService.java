package com.vn.capstone.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.vn.capstone.domain.Product;
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

    public Slide fetchSlideById(long id) {
        Optional<Slide> SlideOptional = this.slideRepository.findById(id);
        if (SlideOptional.isPresent()) {
            return SlideOptional.get();
        }
        return null;
    }

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
