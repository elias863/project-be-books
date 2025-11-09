package com.iliad.library.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "Format")
@Data
public class Format {

    @Id
    private Long id;

    private String textHtml;
    private String applicationEpubZip;
    private String applicationMobiPocket;
    private String textPlainAscii;
    private String textPlainUtf8;
    private String textHtmlCharsetUtf8;
    private String applicationRdfXml;
    private String imageJpeg;
    private String applicationOctetStream;
    private int downloadCount;
}