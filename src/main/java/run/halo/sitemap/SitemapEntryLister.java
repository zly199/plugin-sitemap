package run.halo.sitemap;

import reactor.core.publisher.Flux;
import run.halo.sitemap.vo.SitemapEntry;

public interface SitemapEntryLister {
    Flux<SitemapEntry> list(SitemapGeneratorOptions options);
}
