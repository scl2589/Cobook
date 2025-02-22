package com.ssafy.cobook.controller;

import com.ssafy.cobook.domain.user.User;
import com.ssafy.cobook.service.ProfileService;
import com.ssafy.cobook.service.dto.club.ClubResDto;
import com.ssafy.cobook.service.dto.clubevent.ClubEventSimpleResDto;
import com.ssafy.cobook.service.dto.onedayevent.OneDayEventResponseDto;
import com.ssafy.cobook.service.dto.post.PostResponseDto;
import com.ssafy.cobook.service.dto.profile.ProfileResponseDto;
import com.ssafy.cobook.service.dto.profile.ProfileStatisticsResDto;
import com.ssafy.cobook.service.dto.user.UserByFollowDto;
import com.ssafy.cobook.service.dto.user.UserResponseIdDto;
import com.ssafy.cobook.service.dto.user.UserUpdateReqDto;
import com.ssafy.cobook.util.FileUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    private final ProfileService profileService;
    private final FileUtil fileService;

    @ApiOperation(value = "자신의 회원정보를 수정한다")
    @ApiImplicitParams({@ApiImplicitParam(name = "jwt", value = "JWT Token", required = true, dataType = "string", paramType = "header")})
    @PutMapping()
    public ResponseEntity<UserResponseIdDto> updateProfile(@ApiIgnore final Authentication authentication,
                                                           @RequestBody @Valid final UserUpdateReqDto userUpdateDto) {
        Long userId = ((User) authentication.getPrincipal()).getId();
        return ResponseEntity.status(HttpStatus.OK).body(profileService.updateUserInfo(userId, userUpdateDto));
    }

    @ApiOperation(value = "사용자의 이미지를 저장한다")
    @ApiImplicitParams({@ApiImplicitParam(name = "jwt", value = "JWT Token", required = true, dataType = "string", paramType = "header")})
    @PostMapping("/images")
    public ResponseEntity<Void> updateUserPicture(@ApiIgnore final Authentication authentication,
                                                  @RequestParam final MultipartFile profileImg) throws IOException {
        Long userId = ((User) authentication.getPrincipal()).getId();
        profileService.saveImg(userId, profileImg);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "사용자의 이미지를 가져온다")
    @GetMapping("/images/{userId}")
    public ResponseEntity<Resource> getProfileImg(@PathVariable("userId") final Long userId, HttpServletRequest request) {
        String path = profileService.getFilePath(userId);
        Resource resource = fileService.loadFileAsResource(path);
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @ApiOperation(value = "특정 회원의 정보를 가져온다")
    @ApiImplicitParams({@ApiImplicitParam(name = "jwt", value = "JWT Token", required = true, dataType = "string", paramType = "header")})
    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResponseDto> getUserInfo(@ApiIgnore Authentication authentication, @PathVariable("userId") Long toUserId) {
        Long fromUserId =  ((User) authentication.getPrincipal()).getId();
        return ResponseEntity.status(HttpStatus.OK).body(profileService.getUserInfo(fromUserId, toUserId));
    }

    @ApiOperation(value = "팔로우 버튼을 누른다")
    @ApiImplicitParams({@ApiImplicitParam(name = "jwt", value = "JWT Token", required = true, dataType = "string", paramType = "header")})
    @GetMapping("/{userId}/follow")
    public ResponseEntity<Void> followUser(@ApiIgnore Authentication authentication, @PathVariable("userId") Long toUserId){
        Long fromUserId =  ((User) authentication.getPrincipal()).getId();
        profileService.addFollow(fromUserId, toUserId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "팔로잉한 사람들의 리스트 가져오기")
    @ApiImplicitParams({@ApiImplicitParam(name = "jwt", value = "JWT Token", required = true, dataType = "string", paramType = "header")})
    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserByFollowDto>>getFollowingList(@ApiIgnore Authentication authentication, @PathVariable("userId") Long toUserId){
        Long fromUserId =  ((User) authentication.getPrincipal()).getId();
        return ResponseEntity.status(HttpStatus.OK).body(profileService.getFollowingList(fromUserId, toUserId));
    }


    @ApiOperation(value = "팔로우한 사람들의 리스트가져오기")
    @ApiImplicitParams({@ApiImplicitParam(name = "jwt", value = "JWT Token", required = true, dataType = "string", paramType = "header")})
    @GetMapping("/{userId}/follower")
    public ResponseEntity<List<UserByFollowDto>>getFollowerList(@ApiIgnore Authentication authentication, @PathVariable("userId") Long toUserId){
        Long fromUserId =  ((User) authentication.getPrincipal()).getId();
        return ResponseEntity.status(HttpStatus.OK).body(profileService.getFollowerList(fromUserId, toUserId));
    }

    @ApiOperation(value = "해당 유저의 피드를 가져온다")
    @GetMapping("/{userId}/feed")
    public ResponseEntity<List<PostResponseDto>>getFeed(@PathVariable("userId") Long toUserId){
        return ResponseEntity.status(HttpStatus.OK).body(profileService.getUserFeed(toUserId));
    }

    @ApiOperation(value = "해당 유저의 클럽을 가져온다")
    @GetMapping("/{userId}/club")
    public ResponseEntity<List<ClubResDto>>getClub(@PathVariable("userId") Long toUserId){
        return ResponseEntity.status(HttpStatus.OK).body(profileService.getUserClub(toUserId));
    }


    @ApiOperation(value = "해당 유저의 북마크를 가져온다")
    @GetMapping("/{userId}/bookmark")
    public ResponseEntity<List<PostResponseDto>> getBookmark(@PathVariable("userId") Long toUserId) {
        return ResponseEntity.status(HttpStatus.OK).body(profileService.getUserBookmark(toUserId));
    }

    @ApiOperation(value = "해당 유저의 클럽 이벤트를 가져온다")
    @GetMapping("/{userId}/clubevents")
    public ResponseEntity<List<ClubEventSimpleResDto>>getClubEvents(@PathVariable("userId") Long toUserId){
        return ResponseEntity.status(HttpStatus.OK).body(profileService.getClubEvents(toUserId));
    }

    @ApiOperation(value = "해당 유저의 원데이 이벤트를 가져온다")
    @GetMapping("/{userId}/onedayevents")
    public ResponseEntity<List<OneDayEventResponseDto>>getOneDayEvents(@PathVariable("userId") Long toUserId){
        return ResponseEntity.status(HttpStatus.OK).body(profileService.getOneDayEvents(toUserId));
    }

    @ApiOperation(value = "해당 유저의 통계 정보를 가져온다")
    @GetMapping("/{userId}/overview")
    public ResponseEntity<ProfileStatisticsResDto> getUserStatics(@PathVariable("userId") Long toUserId) {
        return ResponseEntity.status(HttpStatus.OK).body(profileService.getUserStatics(toUserId));
    }
}
