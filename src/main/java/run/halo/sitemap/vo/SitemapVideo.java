package run.halo.sitemap.vo;

import lombok.Data;

@Data
public class SitemapVideo {
    private String thumbnailLoc;    // 视频缩略图 URL
    private String title;           // 视频标题
    private String description;     // 视频描述
    private String contentLoc;      // 视频内容 URL
    private String publicationDate; // 视频发布时间（ISO 8601 格式）
    private int viewCount;          // 播放次数（可选）

    public SitemapVideo(String thumbnailLoc, String title, String description, String contentLoc,  String publicationDate) {
        this.thumbnailLoc = thumbnailLoc;
        this.title = title;
        this.description = description;
        this.contentLoc = contentLoc;
        this.publicationDate = publicationDate;
    }
}
