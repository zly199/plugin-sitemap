package run.halo.sitemap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import run.halo.sitemap.vo.SitemapEntry;
import run.halo.sitemap.vo.SitemapImage;
import run.halo.sitemap.vo.SitemapVideo;

/**
 * Builder class to generate xml for sitemap.
 *
 * @author guqing
 * @since 1.0.0
 */
public class SitemapBuilder {

    public String withXMLTemplate(String content) {
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"
                    xmlns:news="http://www.google.com/schemas/sitemap-news/0.9"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd"
                    xmlns:mobile="http://www.google.com/schemas/sitemap-mobile/1.0"
                    xmlns:image="http://www.google.com/schemas/sitemap-image/1.1"
                    xmlns:video="http://www.google.com/schemas/sitemap-video/1.1">
            %s
            </urlset>
            """.formatted(content);
    }




    public String buildSitemapXml(List<SitemapEntry> sitemapEntries) {
        StringBuilder content = new StringBuilder();

        // 添加 XML 声明和根元素 <urlset>，并声明 image 和 video 命名空间
        content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        content.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" ");
        content.append("xmlns:image=\"http://www.google.com/schemas/sitemap-image/1.1\" ");
        content.append("xmlns:video=\"http://www.google.com/schemas/sitemap-video/1.1\">");

        // 遍历每个 sitemap 条目
        for (SitemapEntry entry : sitemapEntries) {
            StringBuilder urlContent = new StringBuilder();

            // 基本字段（使用 escapeXml11 转义特殊字符）
            urlContent.append(String.format("<loc>%s</loc>", StringEscapeUtils.escapeXml11(entry.getLoc())));
            if (StringUtils.isNotBlank(entry.getLastmod())) {
                urlContent.append(String.format("<lastmod>%s</lastmod>", StringEscapeUtils.escapeXml11(entry.getLastmod())));
            }
            if (entry.getChangefreq() != null) {
                urlContent.append(String.format("<changefreq>%s</changefreq>",
                    StringEscapeUtils.escapeXml11(entry.getChangefreq().name().toLowerCase())));
            }
            if (entry.getPriority() != null) {
                urlContent.append(String.format("<priority>%s</priority>",
                    StringEscapeUtils.escapeXml11(entry.getPriority().toString())));
            }

            // 图片信息
            if (entry.getImages() != null && !entry.getImages().isEmpty()) {
                for (SitemapImage image : entry.getImages()) {
                    urlContent.append("<image:image>");
                    urlContent.append(String.format("<image:loc>%s</image:loc>",
                        StringEscapeUtils.escapeXml11(image.getLoc())));
                    if (StringUtils.isNotBlank(image.getTitle())) {
                        urlContent.append(String.format("<image:title>%s</image:title>",
                            StringEscapeUtils.escapeXml11(image.getTitle())));
                    }
                    if (StringUtils.isNotBlank(image.getCaption())) {
                        urlContent.append(String.format("<image:caption>%s</image:caption>",
                            StringEscapeUtils.escapeXml11(image.getCaption())));
                    }
                    urlContent.append("</image:image>");
                }
            }

            // 视频信息
            if (entry.getVideos() != null && !entry.getVideos().isEmpty()) {
                for (SitemapVideo video : entry.getVideos()) {
                    urlContent.append("<video:video>");
                    if (StringUtils.isNotBlank(video.getThumbnailLoc())) {
                        urlContent.append(String.format("<video:thumbnail_loc>%s</video:thumbnail_loc>",
                            StringEscapeUtils.escapeXml11(video.getThumbnailLoc())));
                    }
                    if (StringUtils.isNotBlank(video.getTitle())) {
                        urlContent.append(String.format("<video:title>%s</video:title>",
                            StringEscapeUtils.escapeXml11(video.getTitle())));
                    }
                    if (StringUtils.isNotBlank(video.getDescription())) {
                        urlContent.append(String.format("<video:description>%s</video:description>",
                            StringEscapeUtils.escapeXml11(video.getDescription())));
                    }
                    if (StringUtils.isNotBlank(video.getContentLoc())) {
                        urlContent.append(String.format("<video:content_loc>%s</video:content_loc>",
                            StringEscapeUtils.escapeXml11(video.getContentLoc())));
                    }
                    if (StringUtils.isNotBlank(video.getPublicationDate())) {
                        urlContent.append(String.format("<video:publication_date>%s</video:publication_date>",
                            StringEscapeUtils.escapeXml11(video.getPublicationDate())));
                    }
                    urlContent.append("</video:video>");
                }
            }

            content.append("<url>").append(urlContent).append("</url>");
        }

        content.append("</urlset>");
        return content.toString();
    }

}
