package com.homework4.workapi.config;

import com.homework4.workapi.service.PostSearchReindexService;
import com.homework4.workapi.service.PostSearchReindexService.ReindexBatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("search-reindex")
@Order(Ordered.LOWEST_PRECEDENCE)
@RequiredArgsConstructor
public class PostSearchReindexRunner implements ApplicationRunner {

    private final PostSearchReindexService reindexService;
    private final ConfigurableApplicationContext applicationContext;

    @Value("${app.elasticsearch.post-search.reindex.batch-size:500}")
    private int batchSize;

    @Override
    public void run(ApplicationArguments args) {
        try {
            validateBatchSize();

            if (args.containsOption("clear-before-reindex")) {
                log.warn("기존 게시글 검색 인덱스를 삭제하고 전체 재색인을 시작합니다.");
                reindexService.clearIndex();
            }

            Long lastPostId = 0L;
            int indexedCount = 0;
            int batchCount = 0;

            while (true) {
                ReindexBatch batch = reindexService.reindexAfter(
                        lastPostId,
                        batchSize
                );
                indexedCount += batch.indexedCount();
                if (batch.indexedCount() > 0) {
                    batchCount++;
                }

                if (!batch.hasNext()) {
                    break;
                }

                lastPostId = batch.lastPostId();
            }

            log.info(
                    "게시글 전체 재색인이 완료되었습니다. indexedCount={}, batchCount={}",
                    indexedCount,
                    batchCount
            );
            SpringApplication.exit(applicationContext, () -> 0);
        } catch (Exception exception) {
            log.error("게시글 전체 재색인에 실패했습니다.", exception);
            SpringApplication.exit(applicationContext, () -> 1);
            throw exception;
        }
    }

    private void validateBatchSize() {
        if (batchSize < 1 || batchSize > 5_000) {
            throw new IllegalArgumentException(
                    "app.elasticsearch.post-search.reindex.batch-size는 1 이상 5000 이하이어야 합니다."
            );
        }
    }
}
