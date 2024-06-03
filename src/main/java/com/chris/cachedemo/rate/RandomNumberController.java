package com.chris.cachedemo.rate;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/api/random-number")
@RequiredArgsConstructor
public class RandomNumberController {

  public record NumberDto(int number) {}

  public record ErrorDto(
      int status,
      String error,
      String message,
      long timestamp
  ) {
    static ErrorDto build() {
      return new ErrorDto(
          HttpStatus.TOO_MANY_REQUESTS.value(),
          HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
          "please try again later",
          System.currentTimeMillis()
      );
    }
  }

  private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

  private Bucket resolveBucket(ServletRequest request) {
    Bucket bucket = cache.get(request.getRemoteAddr());
    if (bucket != null)
      return bucket;

    cache.put(request.getRemoteAddr(), Bucket.builder()
        .addLimit(Bandwidth.classic(10, Refill.intervally(5, Duration.ofSeconds(15))))
        .build());

    return cache.get(request.getRemoteAddr());
  }

  @GetMapping
  public ResponseEntity<Object> getRandomNumber(
      @RequestParam(name = "base", defaultValue = "0") int base,
      @RequestParam(name = "limit", defaultValue = "100") int limit,
      HttpServletRequest request
  ) {

    if (resolveBucket(request).tryConsume(1)) {
      Random random = new Random();
      return ResponseEntity.ok(new NumberDto(random.nextInt(base, limit)));
    }

    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
        .body(ErrorDto.build());
  }
}
