package run.halo.sitemap.vo;

import lombok.Data;

@Data
public class SitemapImage {
    private String loc;      // 图片 URL
    private String title;    // 图片标题（可选）
    private String caption;  // 图片描述（可选）
}
