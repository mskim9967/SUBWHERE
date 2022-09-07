package cau.capstone.ottitor.controller;

import cau.capstone.ottitor.dto.DataResponseDto;
import cau.capstone.ottitor.dto.UploadDto;
import cau.capstone.ottitor.service.UploadService;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/upload")
public class UploadController {

    private final UploadService uploadService;

    @GetMapping("/{*path}")
    public ResponseEntity<Resource> getUploadedFile(@PathVariable("path") String path) {
        Resource resource = uploadService.downloadFile(path.substring(1));
        HttpHeaders header = new HttpHeaders();
        header.add("Content-Type", "image/" + path.substring(path.lastIndexOf(".")) + 1);
        return new ResponseEntity<>(resource, header, HttpStatus.OK);
    }

    @PostMapping("/{path}")
    public DataResponseDto<Object> upload(
        @ModelAttribute UploadDto uploadDto,
        @PathVariable(value = "path", required = false) Optional<String> path,
        @RequestParam(value = "multiple") Boolean multiple
    ) {
        return DataResponseDto.of(multiple ? uploadService.uploadFiles(uploadDto, path)
            : uploadService.uploadFile(uploadDto.getFile(), path));
    }
}
