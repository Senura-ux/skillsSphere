package com.agriapp.service.impl;

import com.agriapp.model.Comment;
import com.agriapp.repository.CommentRepository;
import com.agriapp.service.CommentService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment createComment(Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @Override
    public Optional<Comment> getCommentById(String id) {
        return commentRepository.findById(id);
    }

    @Override
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @Override
    public List<Comment> getCommentsByUserId(String userId) {
        return commentRepository.findByUserId(userId);
    }

    @Override
    public List<Comment> getCommentsByReference(String referenceType, String referenceId) {
        return commentRepository.findByReferenceTypeAndReferenceIdOrderByCreatedAtDesc(referenceType, referenceId);
    }

    @Override
    public List<Comment> getTopLevelCommentsByReference(String referenceType, String referenceId) {
        return commentRepository.findByReferenceTypeAndReferenceIdAndParentCommentIdIsNull(referenceType, referenceId);
    }

    @Override
    public List<Comment> getRepliesByParentCommentId(String parentCommentId) {
        return commentRepository.findByParentCommentId(parentCommentId);
    }

    @Override
    public Comment updateComment(Comment comment) {
        if (!commentRepository.existsById(comment.getId())) {
            throw new RuntimeException("Comment not found with ID: " + comment.getId());
        }
        
        comment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(String id) {
        commentRepository.deleteById(id);
    }

    @Override
    public void likeComment(String commentId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isPresent()) {
            Comment comment = commentOpt.get();
            comment.setLikes(comment.getLikes() + 1);
            commentRepository.save(comment);
        } else {
            throw new RuntimeException("Comment not found with ID: " + commentId);
        }
    }
}