package com.tarento.dexdata.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping(value = "/file")
public class CsvFileUploadController {
	
	private static final Logger logger = LoggerFactory.getLogger(DataSetController.class);
	
	
    @PostMapping("/upload") 
    public ResponseEntity<?> singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
        	logger.debug("Upload File is Empty");
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        
        redirectAttributes.addFlashAttribute("message", "File has Upload Successfull");
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }
}
