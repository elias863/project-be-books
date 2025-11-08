package com.iliad.library.mapper;

import com.iliad.library.dto.FormatDTO;
import com.iliad.library.entity.Format;
import org.springframework.stereotype.Component;

@Component
public class FormatMapper {

    public Format toEntity(FormatDTO dto){
        Format entity = new Format();

        entity.setTextHtml(dto.getTextHtml());
        entity.setApplicationEpubZip(dto.getApplicationEpubZip());
        entity.setApplicationMobiPocket(dto.getApplicationMobiPocket());
        entity.setTextPlainAscii(dto.getTextPlainAscii());
        entity.setTextPlainUtf8(dto.getTextPlainUtf8());
        entity.setTextHtmlCharsetUtf8(dto.getTextHtmlCharsetUtf8());
        entity.setApplicationRdfXml(dto.getApplicationRdfXml());
        entity.setImageJpeg(dto.getImageJpeg());
        entity.setApplicationOctetStream(dto.getApplicationOctetStream());
        entity.setDownloadCount(dto.getDownloadCount());

        return entity;
    }

    public FormatDTO toDto(Format entity){
        FormatDTO dto = new FormatDTO();

        dto.setTextHtml(entity.getTextHtml());
        dto.setApplicationEpubZip(dto.getApplicationEpubZip());
        dto.setApplicationMobiPocket(dto.getApplicationMobiPocket());
        dto.setTextPlainAscii(dto.getTextPlainAscii());
        dto.setTextPlainUtf8(dto.getTextPlainUtf8());
        dto.setTextHtmlCharsetUtf8(dto.getTextHtmlCharsetUtf8());
        dto.setApplicationRdfXml(dto.getApplicationRdfXml());
        dto.setImageJpeg(dto.getImageJpeg());
        dto.setApplicationOctetStream(dto.getApplicationOctetStream());
        dto.setDownloadCount(dto.getDownloadCount());

        return dto;
    }
}
