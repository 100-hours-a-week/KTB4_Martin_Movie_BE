package com.homework4.workapi.repository;

import com.homework4.workapi.document.PostSearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PostSearchRepository extends ElasticsearchRepository<PostSearchDocument, Long>{

}
