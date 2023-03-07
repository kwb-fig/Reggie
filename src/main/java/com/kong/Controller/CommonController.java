package com.kong.Controller;

import com.kong.Exception.FileException;
import com.kong.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String BasePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        String randomUUID = UUID.randomUUID().toString();

        String fileName= randomUUID + suffix;

        File filedir=new File(BasePath);

        if(!filedir.exists()){
            filedir.mkdirs();
        }

        try {
            file.transferTo(new File(BasePath+fileName));
        } catch (IOException e) {
            throw new FileException("上传文件异常");
        }
        return R.success(fileName);
    }

    @GetMapping("/download")
    public void download(HttpServletResponse response,String name) {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(BasePath + name));
            //把读取到的图片用输出流写入servlet对象里
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);

                outputStream.flush();
            }
            fileInputStream.close();
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
            }
        }
    }