package com.homework4.workapi.service;

import com.homework4.workapi.dto.comment.request.CommentRequest;
import com.homework4.workapi.dto.comment.response.CommentResponse;
import com.homework4.workapi.entity.Comment;
import com.homework4.workapi.entity.Post;
import com.homework4.workapi.entity.User;
import com.homework4.workapi.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserService userService;
    private static final int COMMENT_PAGE_SIZE = 20;

    @Transactional
    public CommentResponse addComment(CommentRequest commentRequest, Long postId, Long userId) {
        User user = userService.findUserById(userId);
        Post post = postService.findPostById(postId);

        Comment comment = new Comment(user, post, commentRequest.content());

        Comment savedComment = commentRepository.save(comment);
        return CommentResponse.from(savedComment);
    }

    public Page<CommentResponse> getComments(Long postId, int page) {
        if(page<1){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "페이지는 1이상 이어야 합니다.");
        }

        postService.findPostById(postId);

        Pageable pageable = PageRequest.of(page-1, COMMENT_PAGE_SIZE, Sort.by(Sort.Order.desc("createTime"), Sort.Order.desc("id")));

        return commentRepository
                .findByPost_Id(postId, pageable)
                .map(CommentResponse::from);
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, Long userId) {
        Comment comment = findCommentById(commentId);
        if (!comment.getPost().getId().equals(postId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다.");
        }
        if(!comment.isWritten(userId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "댓글 작성자만 삭제 할 수 있습니다.");
        }
        commentRepository.delete(comment);
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, CommentRequest commentRequest, Long postId, Long userId) {
        Comment comment = findCommentById(commentId);

        if(!comment.getPost().getId().equals(postId)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다.");
        }
        if(!comment.isWritten(userId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "댓글 작성자만 수정 할 수 있습니다.");
        }

        comment.updateComment(commentRequest.content());
        return CommentResponse.from(comment);
    }

    public Comment findCommentById(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if(comment.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 없습니다.");
        }
        return comment.get();
    }
}
