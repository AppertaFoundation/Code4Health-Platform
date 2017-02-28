package org.code4health.repository.search;

import org.code4health.domain.Operino;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Operino entity.
 */
public interface OperinoSearchRepository extends ElasticsearchRepository<Operino, Long> {
}
