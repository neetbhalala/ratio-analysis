package com.example.FinanceAnalyzer.controller;

import com.example.FinanceAnalyzer.service.ExtractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/financial")
public class ExtractionController {

    @Autowired
    private ExtractionService extractionService;

    @RequestMapping(method = RequestMethod.POST, value = "/extract-data/{companyName}")
    public ResponseEntity<String> extractFinancialData(@RequestParam("file") MultipartFile file, @PathVariable String companyName) {
        try {
            extractionService.processPDF(file, companyName);
            return ResponseEntity.ok("Financial data extracted and stored successfully. ");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing file: " + e.getMessage());
        }
    }
}