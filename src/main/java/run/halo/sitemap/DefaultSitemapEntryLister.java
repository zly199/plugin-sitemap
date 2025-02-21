package run.halo.sitemap;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.halo.app.core.extension.content.Category;
import run.halo.app.core.extension.content.Post;
import run.halo.app.core.extension.content.SinglePage;
import run.halo.app.core.extension.content.Tag;
import run.halo.app.extension.ConfigMap;
import run.halo.app.extension.ExtensionOperator;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.infra.utils.JsonUtils;
import run.halo.sitemap.dto.UrlEntryMeta;
import run.halo.sitemap.vo.SitemapEntry;
import run.halo.sitemap.vo.SitemapImage;
import run.halo.sitemap.vo.SitemapVideo;

@Component
@AllArgsConstructor
public class DefaultSitemapEntryLister implements SitemapEntryLister {

    private final ReactiveExtensionClient client;

    @Override
    public Flux<SitemapEntry> list(SitemapGeneratorOptions options) {
        return Flux.mergeSequential(listPostUrls(),
                listCategoryUrls(),
                listTagUrls(),
                listSinglePageUrls(),
                urlsForListPages(),
                listHomeUrls())
            .distinct()
            .map(options::transform);
    }

    //增加主页
    private Flux<UrlEntryMeta> listHomeUrls() {
        return Flux.just(new UrlEntryMeta("/",1.0,ChangeFreqEnum.DAILY));
    }

    private Flux<UrlEntryMeta> listPostUrls() {
        return client.list(Post.class, post -> post.isPublished() && !post.isDeleted()
                    && Post.VisibleEnum.PUBLIC.equals(post.getSpec().getVisible()),
                defaultComparator())
            .map(post -> {
                var status = post.getStatusOrDefault();
                UrlEntryMeta urlEntryMeta =
                    new UrlEntryMeta(status.getPermalink(), 0.8, ChangeFreqEnum.MONTHLY)
                        .setLastModifiedTime(status.getLastModifyTime());
                String imgUrl = post.getMetadata().getAnnotations().getOrDefault("mainUrl", "");
                String videoUrl = post.getMetadata().getAnnotations().getOrDefault("coverGIf", "");
                if (StringUtils.isNotBlank(imgUrl)) {
                    urlEntryMeta.getImages().add(new SitemapImage(imgUrl, post.getSpec().getTitle(), post.getSpec().getTitle()));
                }
                if (StringUtils.isNotBlank(videoUrl)) {
                    urlEntryMeta.getVideos().add(
                        new SitemapVideo(post.getSpec().getCover(), post.getSpec().getTitle(), post.getSpec().getTitle(),
                            videoUrl, Instant.now().toString()));
                }

                return urlEntryMeta;

                }
            );
    }

    Comparator<Post> defaultComparator() {
        Function<Post, Instant> createTime = post -> post.getMetadata().getCreationTimestamp();
        Function<Post, String> name = post -> post.getMetadata().getName();
        return Comparator.comparing(createTime).thenComparing(name);
    }

    private Flux<UrlEntryMeta> listSinglePageUrls() {
        return client.list(SinglePage.class, singlePage -> singlePage.isPublished()
                    && Objects.equals(false, singlePage.getSpec().getDeleted())
                    && ExtensionOperator.isNotDeleted().test(singlePage)
                    && Post.VisibleEnum.PUBLIC.equals(singlePage.getSpec().getVisible()),
                pageDefaultComparator())
            .map(SinglePage::getStatusOrDefault)
            .map(status -> new UrlEntryMeta(status.getPermalink(),0.6,ChangeFreqEnum.WEEKLY)
                .setLastModifiedTime(status.getLastModifyTime())
            );
    }

    Comparator<SinglePage> pageDefaultComparator() {
        Function<SinglePage, Instant> createTime =
            page -> page.getMetadata().getCreationTimestamp();
        Function<SinglePage, String> name = page -> page.getMetadata().getName();
        return Comparator.comparing(createTime).thenComparing(name);
    }

    private Flux<UrlEntryMeta> listCategoryUrls() {
        return client.list(Category.class,
                category -> category.getMetadata().getDeletionTimestamp() == null,
                Comparator.comparing(tag -> tag.getMetadata().getCreationTimestamp()))
            .map(Category::getStatusOrDefault)
            .map(status -> new UrlEntryMeta(status.getPermalink(),0.7,ChangeFreqEnum.DAILY));
    }

    private Flux<UrlEntryMeta> listTagUrls() {
        return client.list(Tag.class,
                tag -> tag.getMetadata().getDeletionTimestamp() == null,
                Comparator.comparing(tag -> tag.getMetadata().getCreationTimestamp()))
            .map(Tag::getStatusOrDefault)
            .map(status -> new UrlEntryMeta(status.getPermalink(),0.5,ChangeFreqEnum.DAILY));
    }

    private Flux<UrlEntryMeta> urlsForListPages() {
        // TODO 优化系统其他路由获取
        return client.fetch(ConfigMap.class, "system")
            .mapNotNull(ConfigMap::getData)
            .map(data -> {
                String routeRuleConfig = data.get(ThemeRouteRules.GROUP);
                ThemeRouteRules themeRouteRules =
                    JsonUtils.jsonToObject(routeRuleConfig, ThemeRouteRules.class);
                return List.of(StringUtils.prependIfMissing(themeRouteRules.getTags(), "/"),
                    StringUtils.prependIfMissing(themeRouteRules.getCategories(), "/"),
                    StringUtils.prependIfMissing(themeRouteRules.getArchives(), "/")
                );
            })
            .flatMapMany(Flux::fromIterable)
            .map(url -> new UrlEntryMeta(url,0.3,ChangeFreqEnum.MONTHLY));
    }

    @Data
    public static class ThemeRouteRules {
        public static final String GROUP = "routeRules";

        private String categories;
        private String archives;
        private String post;
        private String tags;
    }
}
