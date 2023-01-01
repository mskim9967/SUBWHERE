//package cau.capstone.ottitor.service;
//
//import cau.capstone.ottitor.constant.Code;
//import cau.capstone.ottitor.dto.UploadDto;
//import cau.capstone.ottitor.util.GeneralException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.Resource;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//@Slf4j
//@RequiredArgsConstructor
//@Transactional
//@Service
//public class UploadService {
//
//    private final S3UploaderService s3UploaderService;
//
//    @Value("${upload.url}")
//    private String uploadUrl;
//
//    /**
//     * 메이슨) 복수 파일들을 업로드합니다
//     *
//     * @param uploadDto 업로드할 파일들 body
//     * @param path      s3 경로
//     * @return 업로드 url들 -> UploadDto.urls
//     */
//    public UploadDto uploadFiles(UploadDto uploadDto, Optional<String> path) {
//        List<String> uploadUrls = new ArrayList<>();
//
//        Optional.ofNullable(uploadDto.getFiles()).ifPresent(files ->
//            files.forEach(imgFile -> uploadUrls.add(uploadFile(imgFile, path)))
//        );
//        return UploadDto.builder().urls(uploadUrls).build();
//    }
//
//    /**
//     * 메이슨) 단일 파일을 업로드합니다
//     *
//     * @param multipartFile 업로드할 파일
//     * @param path          s3 경로
//     * @return 업로드 url
//     */
//    public String uploadFile(MultipartFile multipartFile, Optional<String> path) {
//        try {
//            Boolean saveThumbnail = path.orElse("").matches("profile|other");
//            return uploadUrl + s3UploaderService.uploadFile(multipartFile, path, saveThumbnail);
//        } catch (Exception e) {
//            log.error("S3 Upload failed", e);
//            throw new GeneralException(Code.S3_UPLOAD_ERROR, e);
//        }
//    }
//
//    public Resource downloadFile(String filename) {
//        return s3UploaderService.downloadFile(filename);
//    }
//}