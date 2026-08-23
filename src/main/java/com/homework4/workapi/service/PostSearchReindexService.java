package com.homework4.workapi.service;

import com.homework4.workapi.document.PostSearchDocument;
import com.homework4.workapi.entity.Post;
import com.homework4.workapi.repository.PostRepository;
import com.homework4.workapi.repository.PostSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostSearchReindexService {

    private final PostRepository postRepository;
    private final PostSearchRepository postSearchRepository;

    @Transactional(readOnly = true)
    public ReindexBatch reindexAfter(Long lastPostId, int batchSize) {
        Pageable pageable = PageRequest.of(0, batchSize + 1);
        List<Post> candidates = postRepository
                .findByIdGreaterThanOrderByIdAsc(lastPostId, pageable);

        if (candidates.isEmpty()) {
            return new ReindexBatch(lastPostId, 0, false);
        }

        boolean hasNext = candidates.size() > batchSize;
        List<Post> batch = hasNext
                ? candidates.subList(0, batchSize)
                : candidates;

        postSearchRepository.saveAll(
                batch.stream()
                        .map(PostSearchDocument::from)
                        .toList()
        );

        Long nextPostId = batch.get(batch.size() - 1).getId();
        return new ReindexBatch(nextPostId, batch.size(), hasNext);
    }

    public void clearIndex() {
        postSearchRepository.deleteAll();
    }

    public record ReindexBatch(
            Long lastPostId,
            int indexedCount,
            boolean hasNext
    ) {
    }
}
