package com.example.explor_gastro.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class CommentDTO {
    private String comments;
    private String imgId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;
    private String userName;
    private String userAvatar;
    private Integer id;
    private Integer score;
}