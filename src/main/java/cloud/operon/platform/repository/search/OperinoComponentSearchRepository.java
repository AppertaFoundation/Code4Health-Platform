package cloud.operon.platform.repository.search;

import cloud.operon.platform.domain.OperinoComponent;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the OperinoComponent entity.
 */
public interface OperinoComponentSearchRepository extends ElasticsearchRepository<OperinoComponent, Long> {
}
