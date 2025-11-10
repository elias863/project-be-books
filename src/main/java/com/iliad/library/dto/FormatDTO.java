package com.iliad.library.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FormatDTO {

    @JsonProperty("text/html")
    private String textHtml;

    @JsonProperty("application/epub+zip")
    private String applicationEpubZip;

    @JsonProperty("application/x-mobipocket-ebook")
    private String applicationMobiPocket;

    @JsonProperty("text/plain; charset=us-ascii")
    private String textPlainAscii;

    @JsonProperty("text/plain; charset=utf-8")
    private String textPlainUtf8;

    @JsonProperty("text/html; charset=utf-8")
    private String textHtmlCharsetUtf8;

    @JsonProperty("application/rdf+xml")
    private String applicationRdfXml;

    @JsonProperty("image/jpeg")
    private String imageJpeg;

    @JsonProperty("application/octet-stream")
    private String applicationOctetStream;

    @JsonProperty("download_count")
    private int downloadCount;

    @JsonProperty("application/pdf")
    private String applicationPdf;

    @JsonProperty("application/msword")
    private String applicationMsword;

    @JsonProperty("application/prs.tei")
    private String applicationPrsTei;
}
