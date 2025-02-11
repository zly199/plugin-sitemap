package run.halo.sitemap.dto;

import io.micrometer.common.util.StringUtils;
import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import run.halo.sitemap.ChangeFreqEnum;
import run.halo.sitemap.SitemapGeneratorOptions;
import run.halo.sitemap.vo.SitemapImage;
import run.halo.sitemap.vo.SitemapVideo;

@Data
@Accessors(chain = true)
public class UrlEntryMeta {
    private String url;

    /**
     * see also {@link SitemapGeneratorOptions#getPriority()}.
     */
    private Double priority;

    private Instant lastModifiedTime;

    private List<SitemapImage> images; // 图片信息列表

    private List<SitemapVideo> videos; // 视频信息列表

    private ChangeFreqEnum changefreq;

    public UrlEntryMeta(String url,Double priority,ChangeFreqEnum changefreq) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url must not be blank");
        }
        this.priority = priority;
        this.url = url;
        this.changefreq = changefreq;
    }
}
