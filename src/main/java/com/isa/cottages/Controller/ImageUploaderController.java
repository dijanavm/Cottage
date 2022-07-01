package com.isa.cottages.Controller;

import com.isa.cottages.Service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ImageUploaderController {

    private UserServiceImpl userService;

    @Autowired
    public ImageUploaderController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/getImage/{imageUrl}/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ByteArrayResource> getImage(@PathVariable("imageUrl") String imageUrl, @PathVariable("id") Long id,
                                                      Model model) throws  Exception{
        if(imageUrl.equals("") || imageUrl != null) {
            try {
                Path filename = Paths.get("C:\\Users\\Dijana\\Desktop\\Cottages\\cottages\\uploads", imageUrl);
                byte[] buffer = Files.readAllBytes(filename);
                ByteArrayResource byteArrayResource = new ByteArrayResource(buffer);
                return ResponseEntity.ok()
                        .contentLength(buffer.length)
                        .contentType(MediaType.parseMediaType("image/jpg"))
                        .body(byteArrayResource);
            } catch (Exception e) {
            }
        }
        return ResponseEntity.badRequest().build();
    }
}
