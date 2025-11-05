package com.vn.capstone.domain.response.slide;

import java.time.Instant;

import com.vn.capstone.util.constant.SlideType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ResSlideDTO {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String redirectUrl;

    private Boolean active = true;
    private Integer orderIndex;
    private SlideType type; // "HOME", "ABOUT", "CONTACT"

    private Instant createdAt;
    private Instant updatedAt;
}
