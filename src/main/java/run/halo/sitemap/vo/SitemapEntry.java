package run.halo.sitemap.vo;

import lombok.Builder;
import lombok.Data;
import run.halo.sitemap.ChangeFreqEnum;
import java.util.List;

/**
 * @author guqing
 * @since 1.0.0
 */
@Data
@Builder
public class SitemapEntry {
    /**
     * <p>Parent tag for each URL entry. The remaining tags are children of this tag.</p>
     * required.
     */
    private String loc;

    private String lastmod;

    private ChangeFreqEnum changefreq;

    private Double priority;

    private List<SitemapImage> images; // 图片信息列表

    private List<SitemapVideo> videos; // 视频信息列表

}
