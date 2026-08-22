package com.homework4.workapi.config;

import com.homework4.workapi.document.PostSearchDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "app.elasticsearch.post-search",
        name = "ensure-index-on-startup",
        havingValue = "true",
        matchIfMissing = true
)
public class PostSearchIndexInitializer implements ApplicationRunner {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public void run(ApplicationArguments args) {
        IndexOperations indexOperations =
                elasticsearchOperations.indexOps(PostSearchDocument.class);

        if (indexOperations.exists()) {
            log.info("게시글 검색 인덱스가 이미 존재합니다.");
            return;
        }

        if (!indexOperations.createWithMapping()) {
            throw new IllegalStateException(
                    "게시글 검색 인덱스 생성에 실패했습니다."
            );
        }

        log.info("게시글 검색 인덱스와 Nori 매핑을 생성했습니다.");
    }
}
