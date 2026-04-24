package com.example.demo.controller;

import com.example.demo.dto.BfhlRequest;
import com.example.demo.dto.BfhlResponse;
import com.example.demo.service.AiService;
import com.example.demo.service.BfhlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class BfhlController {

    private static final String EMAIL = "jatin2026.be23@chitkara.edu.in";

    @Autowired
    private BfhlService bfhlService;

    @Autowired
    private AiService aiService;

    // -----------------------------------------------------------------------
    // GET /health
    // -----------------------------------------------------------------------
    @GetMapping("/health")
    public ResponseEntity<BfhlResponse> health() {
        BfhlResponse response = new BfhlResponse();
        response.setIs_success(true);
        response.setOfficial_email(EMAIL);
        return ResponseEntity.ok(response);
    }

    // -----------------------------------------------------------------------
    // POST /bfhl
    // -----------------------------------------------------------------------
    @PostMapping("/bfhl")
    public ResponseEntity<BfhlResponse> processRequest(@RequestBody BfhlRequest request) {

        // Guard: body must not be null
        if (request == null) {
            return errorResponse("Request body is missing", HttpStatus.BAD_REQUEST);
        }

        // Count how many top-level keys were provided
        int keyCount = 0;
        if (request.getFibonacci() != null) keyCount++;
        if (request.getPrime() != null)     keyCount++;
        if (request.getLcm() != null)       keyCount++;
        if (request.getHcf() != null)       keyCount++;
        if (request.getAI() != null)        keyCount++;

        if (keyCount == 0) {
            return errorResponse("Request must contain exactly one key: fibonacci, prime, lcm, hcf, or AI",
                    HttpStatus.BAD_REQUEST);
        }
        if (keyCount > 1) {
            return errorResponse("Request must contain exactly one key (multiple keys provided)",
                    HttpStatus.BAD_REQUEST);
        }

        try {
            BfhlResponse response = new BfhlResponse();
            response.setOfficial_email(EMAIL);

            // --- fibonacci ---
            if (request.getFibonacci() != null) {
                int n = request.getFibonacci();
                if (n < 0) {
                    return errorResponse("fibonacci value must be >= 0", HttpStatus.BAD_REQUEST);
                }
                response.setData(bfhlService.calculateFibonacci(n));
            }

            // --- prime ---
            else if (request.getPrime() != null) {
                List<Integer> list = request.getPrime();
                if (list.isEmpty()) {
                    return errorResponse("prime array must not be empty", HttpStatus.BAD_REQUEST);
                }
                response.setData(bfhlService.filterPrimes(list));
            }

            // --- lcm ---
            else if (request.getLcm() != null) {
                List<Integer> list = request.getLcm();
                if (list.isEmpty()) {
                    return errorResponse("lcm array must not be empty", HttpStatus.BAD_REQUEST);
                }
                for (int val : list) {
                    if (val <= 0) {
                        return errorResponse("lcm array values must be positive integers",
                                HttpStatus.BAD_REQUEST);
                    }
                }
                response.setData(bfhlService.calculateLcm(list));
            }

            // --- hcf ---
            else if (request.getHcf() != null) {
                List<Integer> list = request.getHcf();
                if (list.isEmpty()) {
                    return errorResponse("hcf array must not be empty", HttpStatus.BAD_REQUEST);
                }
                for (int val : list) {
                    if (val <= 0) {
                        return errorResponse("hcf array values must be positive integers",
                                HttpStatus.BAD_REQUEST);
                    }
                }
                response.setData(bfhlService.calculateHcf(list));
            }

            // --- AI ---
            else if (request.getAI() != null) {
                String question = request.getAI().trim();
                if (question.isEmpty()) {
                    return errorResponse("AI question must not be blank", HttpStatus.BAD_REQUEST);
                }
                response.setData(aiService.askAI(question));
            }

            response.setIs_success(true);
            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            // AI key not configured
            return errorResponse(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            return errorResponse("Internal server error: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // -----------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------
    private ResponseEntity<BfhlResponse> errorResponse(String message, HttpStatus status) {
        BfhlResponse response = new BfhlResponse();
        response.setIs_success(false);
        response.setOfficial_email(EMAIL);
        response.setMessage(message);
        return new ResponseEntity<>(response, status);
    }
}
