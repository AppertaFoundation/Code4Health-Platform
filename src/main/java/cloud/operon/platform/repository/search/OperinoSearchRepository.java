package cloud.operon.platform.repository.search;

import cloud.operon.platform.domain.Operino;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Operino entity.
 */
public interface OperinoSearchRepository extends ElasticsearchRepository<Operino, Long> {
}
