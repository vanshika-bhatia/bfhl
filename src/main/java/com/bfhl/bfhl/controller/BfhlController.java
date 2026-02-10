package com.bfhl.bfhl.controller;

import com.bfhl.bfhl.service.OpenAIService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/")
@RestController
public class BfhlController {

    private static final String EMAIL = "vanshika1076.be23@chitkara.edu.in";
    private final OpenAIService openAIService;

    public BfhlController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    // -------- HEALTH --------
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("is_success", true);
        res.put("official_email", EMAIL);
        return ResponseEntity.ok(res);
    }

    // -------- BFHL --------
    @PostMapping("/bfhl")
    public ResponseEntity<Map<String, Object>> bfhl(@RequestBody Map<String, Object> body) {

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("is_success", true);
        res.put("official_email", EMAIL);

        try {
            if (body == null || body.size() != 1)
                throw new IllegalArgumentException("Exactly one key is required");

            String key = body.keySet().iterator().next();
            Object value = body.get(key);
            Object data;

            switch (key) {

                case "fibonacci": {
                    int n = ((Number) value).intValue();
                    List<Integer> fib = new ArrayList<>();
                    int a = 0, b = 1;
                    for (int i = 0; i < n; i++) {
                        fib.add(a);
                        int t = a + b;
                        a = b;
                        b = t;
                    }
                    data = fib;
                    break;
                }

                case "prime": {
                    List<Integer> nums = ((List<?>) value).stream()
                            .map(o -> ((Number) o).intValue())
                            .collect(Collectors.toList());
                    data = nums.stream().filter(this::isPrime).toList();
                    break;
                }

                case "lcm": {
                    List<Integer> nums = ((List<?>) value).stream()
                            .map(o -> ((Number) o).intValue())
                            .toList();
                    data = nums.stream().reduce(this::lcm).orElse(0);
                    break;
                }

                case "hcf": {
                    List<Integer> nums = ((List<?>) value).stream()
                            .map(o -> ((Number) o).intValue())
                            .toList();
                    data = nums.stream().reduce(this::gcd).orElse(0);
                    break;
                }

                case "AI": {
                    data = openAIService.askAI(value.toString());
                    break;
                }

                default:
                    throw new IllegalArgumentException("Invalid key");
            }

            res.put("data", data);
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            res.put("is_success", false);
            res.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }

    private boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i * i <= n; i++)
            if (n % i == 0) return false;
        return true;
    }

    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    private int lcm(int a, int b) {
        return (a * b) / gcd(a, b);
    }
}
