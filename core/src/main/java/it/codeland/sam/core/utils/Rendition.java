package it.codeland.sam.core.utils;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.commons.util.PrefixRenditionPicker;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.mime.MimeTypeService;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rendition {
    protected static final Logger LOG = LoggerFactory.getLogger(Rendition.class);
    public static final String METADATA_EXTENTION = "dc:format";
    public static final String SVG_EXTENTION = "image/svg+xml";
    protected static final String DOT = ".";
    protected static final String SLASH = "/";
    protected static final String NAMED_SERVLET_SELECTOR = "transform";
    protected static final String MIME_TYPE_IMAGE_JPEG = "image/jpeg";
    private static final String DAM_SHA1_METADATA = "dam:sha1";
    public static String getRendition(ResourceResolver resourceResolver, String image, String rendition) {
        if (StringUtils.isNotEmpty(image) && StringUtils.isNotEmpty(rendition)) {
            Resource currentResource = resourceResolver.getResource(image);
            if (currentResource != null) {
                Asset asset = currentResource.adaptTo(Asset.class);
                if (asset != null
                        && asset.getMetadata(METADATA_EXTENTION).toString().equalsIgnoreCase(SVG_EXTENTION)) {
                    return image;
                }
                PrefixRenditionPicker picker = new PrefixRenditionPicker(rendition, true);
                Resource tempRendition = picker.getRendition(asset);
                if (tempRendition != null) {
                    LOG.debug("image rendition path: {}", tempRendition.getPath());
                    return tempRendition.getPath();
                }
            }
        }
        return image;
    }

    public static String getRenditionByPolicy(final Resource assetResource, String rendition,
            MimeTypeService mimeTypeService) {
        String finalRendition = StringUtils.EMPTY;
        if (assetResource != null) {
            Asset asset = assetResource.adaptTo(Asset.class);
            if (asset != null) {
                String mimeType = PropertiesUtil.toString(asset.getMimeType(), MIME_TYPE_IMAGE_JPEG).split(";")[0];
                String extension = mimeTypeService.getExtension(mimeType);
                String sha1 = asset.getMetadataValue(DAM_SHA1_METADATA);
                String baseResourcePath = asset.getPath();
                if (StringUtils.isNotBlank(rendition)) {
                    finalRendition = baseResourcePath + DOT + NAMED_SERVLET_SELECTOR + SLASH + rendition + SLASH
                            + sha1 + SLASH + "img" + DOT + extension;
                } else {
                    finalRendition = baseResourcePath;
                }
            } else {
                LOG.error("Unable to adapt resource '{}' used by image '{}' to an asset.", assetResource.getPath(),
                        assetResource.getPath());
            }
        }
        return finalRendition;
    }
}
