package com.homework4.workapi.event;

public record PostSearchSyncEvent(
        Long postId,
        Type type
) {
    public enum Type {
        UPSERT,
        DELETE
    }

    public static PostSearchSyncEvent upsert(Long postId) {
        return new PostSearchSyncEvent(postId, Type.UPSERT);
    }

    public static PostSearchSyncEvent delete(Long postId) {
        return new PostSearchSyncEvent(postId, Type.DELETE);
    }
}