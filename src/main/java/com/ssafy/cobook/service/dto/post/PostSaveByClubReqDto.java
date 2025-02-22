package com.ssafy.cobook.service.dto.post;

import com.ssafy.cobook.domain.post.Post;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostSaveByClubReqDto {

    @ApiModelProperty(position = 1)
    private Long bookId;
    @ApiModelProperty(position = 2)
    @Length(min =1, max = 30)
    private String onelineReview;
    @ApiModelProperty(position = 3)
    private Integer rank;
    @ApiModelProperty(position = 4)
    private Boolean open;
    @ApiModelProperty(position = 5)
    @Length(min =1)
    private String review;
    @ApiModelProperty(position = 6)
    private Boolean isClub;
    @ApiModelProperty(position = 7)
    private List<String> tags;

    public Post toEntity() {
        return Post.builder()
                .isClub(true)
                .review(review)
                .rank(rank)
                .open(open)
                .onelineReview(onelineReview)
                .build();
    }
}
