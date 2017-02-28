package org.code4health.repository.search;

import org.code4health.domain.OperinoComponent;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the OperinoComponent entity.
 */
public interface OperinoComponentSearchRepository extends ElasticsearchRepository<OperinoComponent, Long> {
}
