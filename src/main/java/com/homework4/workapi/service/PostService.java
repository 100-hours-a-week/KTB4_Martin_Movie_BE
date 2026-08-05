package com.homework4.workapi.service;

import com.homework4.workapi.event.PostSearchSyncEvent;
import org.springframework.context.ApplicationEventPublisher;
import com.homework4.workapi.dto.post.request.PostRequest;
import com.homework4.workapi.dto.post.response.PostLikeResponse;
import com.homework4.workapi.dto.post.response.PostListResponse;
import com.homework4.workapi.dto.post.response.PostResponse;
import com.homework4.workapi.dto.post.request.UpdatePostRequest;
import com.homework4.workapi.dto.post.response.PostsPreviewResponse;
import com.homework4.workapi.entity.Post;
import com.homework4.workapi.entity.PostLike;
import com.homework4.workapi.entity.PostView;
import com.homework4.workapi.entity.User;
import com.homework4.workapi.projection.CommentCountProjection;
import com.homework4.workapi.repository.CommentRepository;
import com.homework4.workapi.repository.PostLikeRepository;
import com.homework4.workapi.repository.PostRepository;
import com.homework4.workapi.repository.PostViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import static com.homework4.workapi.common.PaginationConstants.POST_PAGE_SIZE;
import static com.homework4.workapi.validation.ValidationConstants.MIN_PAGE;
import static com.homework4.workapi.validation.ValidationConstants.PAGE_MIN_MESSAGE;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final FileService fileService;
    private final PostViewRepository postViewRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PostResponse addPost(Long userId, PostRequest postRequest) {
        User user = userService.findUserById(userId);
        Post post = new Post(user, postRequest.title(), postRequest.content(), postRequest.rating());
        Post savedPost = postRepository.save(post);
        eventPublisher.publishEvent(PostSearchSyncEvent.upsert(savedPost.getId()));

        return PostResponse.from(savedPost, 0, false);
    }

    @Transactional
    public List<PostsPreviewResponse> getPostsPreview(){
        List<Post> posts = postRepository.findTop5ByOrderByCreateTimeDesc();

        if (posts.isEmpty()) {
            return List.of();
        }

        List<Long> postIds = posts.stream()
                .map(Post::getId)
                .toList();

        Map<Long, Integer> commentCountMap =
                commentRepository.countByPostIds(postIds)
                        .stream()
                        .collect(Collectors.toMap(
                                CommentCountProjection::getPostId,
                                result -> Math.toIntExact(
                                        result.getCommentCount()
                                )
                        ));
        return posts.stream()
                .map(post -> {
                    int commentCount =
                            commentCountMap.getOrDefault(
                                    post.getId(),
                                    0
                            );

                    return PostsPreviewResponse.from(post, commentCount);
                }).toList();
    }

    @Transactional
    public Page<PostListResponse> getPosts(Long userId, int page) {
        if (page < MIN_PAGE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    PAGE_MIN_MESSAGE
            );
        }

        Pageable pageable = PageRequest.of(page - MIN_PAGE, POST_PAGE_SIZE, Sort.by(Sort.Order.desc("createTime"), Sort.Order.desc("id")));

        Page<Post> postPage = postRepository.findAllWithUser(pageable);

        List<Long> postIds = postPage.getContent().stream()
                .map(Post::getId)
                .toList();

        Map<Long, Integer> commentCountMap = postIds.isEmpty()
                ? Map.of()
                : commentRepository.countByPostIds(postIds)
                .stream()
                .collect(Collectors.toMap(
                        CommentCountProjection::getPostId,
                        result -> Math.toIntExact(
                                result.getCommentCount()
                        )
                ));

        Set<Long> likedPostIds = postIds.isEmpty()
                ? Set.of()
                : new HashSet<>(
                postLikeRepository.findLikedPostIds(userId, postIds)
        );

        return postPage.map(post ->
                PostListResponse.from(post, commentCountMap.getOrDefault(post.getId(), 0), likedPostIds.contains(post.getId()))
        );
    }


    @Transactional
    public PostResponse getPost(Long postId, Long userId) {
        Post post = findPostById(postId);

        int commentCount = commentRepository.countByPost_Id(postId);

        boolean liked = postLikeRepository
                .findByPost_IdAndUser_Id(postId, userId)
                .isPresent();

        return PostResponse.from(post, commentCount, liked);
    }

    @Transactional
    public PostResponse deletePost(Long postId, Long userId) {
        Post post = findPostById(postId);
        if(!post.isWritten(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "게시글 작성자만 삭제 할 수 있습니다.");
        }

        post.getAttaches().forEach(attach -> {
            fileService.deleteImage(attach.getAttachUrl());
        });

        postRepository.delete(post);
        eventPublisher.publishEvent(PostSearchSyncEvent.delete(post.getId()));


        return PostResponse.from(post, 0, false);
    }

    @Transactional
    public PostResponse updatePost(Long postId, Long userId, UpdatePostRequest postRequest) {
        Post post = findPostById(postId);
        if(!post.isWritten(userId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "게시글 작성자만 수정 가능합니다.");
        }
        post.update(postRequest.title(), postRequest.content(), postRequest.rating());
        eventPublisher.publishEvent(PostSearchSyncEvent.upsert(post.getId()));

        int commentCount = commentRepository.countByPost_Id(postId);
        return PostResponse.from(post, commentCount, false);
    }

    @Transactional
    public PostLikeResponse likePost(Long postId, Long userId) {
        Post post = findPostById(postId);
        User user = userService.findUserById(userId);
        Optional<PostLike> optionalPostLike =
                postLikeRepository.findByPost_IdAndUser_Id(postId, userId);

        if (optionalPostLike.isEmpty()) {
            PostLike postLike = new PostLike(post, user);
            postLikeRepository.save(postLike);
            post.likeIncrease();
        }
        return new PostLikeResponse(post.getLikeCount(), true);
    }

    @Transactional
    public PostLikeResponse unlikePost(Long postId, Long userId) {
        Post post = findPostById(postId);
        userService.findUserById(userId);

        Optional<PostLike> optionalPostLike =
                postLikeRepository.findByPost_IdAndUser_Id(postId, userId);

        if (optionalPostLike.isPresent()) {
            PostLike postLike = optionalPostLike.get();
            postLikeRepository.delete(postLike);
            post.likeDecrease();
        }
        return new PostLikeResponse(post.getLikeCount(), false);
    }

    @Transactional
    public long updatePostView(Long postId, Long userId) {
        Post post = findPostById(postId);
        User user = userService.findUserById(userId);

        LocalDate today = LocalDate.now();
        Optional<PostView> existingView = postViewRepository.findByPost_IdAndUser_Id(postId, userId);

        if (existingView.isEmpty()) {
            PostView postView = new PostView(post, user, today);
            postViewRepository.save(postView);
            post.viewCountIncrease();

            return post.getViewCount();
        }

        PostView postView = existingView.get();

        if (!postView.hasViewedOn(today)) {
            postView.updateViewAt(today);
            post.viewCountIncrease();
        }

        return post.getViewCount();
    }

    public Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
    }
}
