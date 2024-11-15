package com.example.back_end.service.picture;

import com.example.back_end.core.admin.picture.payload.response.PictureResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PictureService {

    List<Long> savePicture(List<MultipartFile> images);

    PictureResponse getPictureById(Long id);

    String savePicture(MultipartFile images);

}
