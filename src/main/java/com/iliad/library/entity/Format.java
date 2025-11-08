package com.iliad.library.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "format")
@Data
public class Format {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long formatId;

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