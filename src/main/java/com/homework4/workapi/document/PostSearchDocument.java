
package com.homework4.workapi.document;

import com.homework4.workapi.entity.Post;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Document(indexName = "workapi-posts-search", createIndex = false, writeTypeHint = WriteTypeHint.FALSE)
@Setting(settingPath = "/elasticsearch/post-search-settings.json")
@Mapping(mappingPath = "/elasticsearch/post-search-mapping.json")
public class PostSearchDocument {

    @Id
    @Field(type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Text, analyzer = "posts_nori", searchAnalyzer = "posts_nori")
    private String title;

    @Field(type = FieldType.Text, analyzer = "posts_nori", searchAnalyzer = "posts_nori")
    private String content;

    @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime createTime;

    public static PostSearchDocument from(Post post) {
        return new PostSearchDocument(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreateTime()
        );
    }
}
