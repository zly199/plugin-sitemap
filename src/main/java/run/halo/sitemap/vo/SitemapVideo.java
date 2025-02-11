package run.halo.sitemap.vo;

import lombok.Data;

@Data
public class SitemapVideo {
    private String thumbnailLoc;    // 视频缩略图 URL
    private String title;           // 视频标题
    private String description;     // 视频描述
    private String contentLoc;      // 视频内容 URL
    private String playerLoc;       // 视频播放器 URL（可选）
    private int duration;           // 视频时长（秒）
    private String publicationDate; // 视频发布时间（ISO 8601 格式）
    private int viewCount;          // 播放次数（可选）
}
