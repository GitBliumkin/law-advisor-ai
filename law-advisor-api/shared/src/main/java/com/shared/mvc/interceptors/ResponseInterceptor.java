package com.shared.mvc.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

public class ResponseInterceptor implements HandlerInterceptor {
  private static final Logger log = LoggerFactory.getLogger(ResponseInterceptor.class);

  private static final String START_TIME = "reqStartTimeNanos";
  private static final String REQ_ID_HEADER = "X-Request-Id";
  private static final String MDC_REQUEST_ID = "requestId";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    // Correlation id (reuse inbound header or generate)
    String requestId = headerOrDefault(request, REQ_ID_HEADER, UUID.randomUUID().toString());
    response.setHeader(REQ_ID_HEADER, requestId);
    MDC.put(MDC_REQUEST_ID, requestId);

    request.setAttribute(START_TIME, System.nanoTime());

    String method = request.getMethod();
    String uri = request.getRequestURI();
    String qs = request.getQueryString();
    String ip = clientIp(request);
    String ua = safeUA(request);

    log.info("→ {} {}{} ip={} ua={}",
        method, uri, (qs == null ? "" : "?" + qs), ip, ua);

    return true; // proceed
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    try {
      long start = (Long) request.getAttribute(START_TIME);
      long tookMs = (System.nanoTime() - start) / 1_000_000;
      int status = response.getStatus();

      if (ex != null) {
        log.error("← {} {} status={} took={}ms EXCEPTION: {}",
            request.getMethod(), request.getRequestURI(), status, tookMs, ex.toString(), ex);
      } else {
        log.info("← {} {} status={} took={}ms",
            request.getMethod(), request.getRequestURI(), status, tookMs);
      }
    } catch (Throwable swallow) {
      // never let logging break the request lifecycle
      log.debug("Interceptor logging failed", swallow);
    } finally {
      MDC.remove(MDC_REQUEST_ID);
    }
  }

  // --- helpers ---

  private static String headerOrDefault(HttpServletRequest req, String name, String def) {
    String v = req.getHeader(name);
    return (v == null || v.isBlank()) ? def : v;
  }

  private static String clientIp(HttpServletRequest req) {
    // honor common proxy headers, fallback to remote addr
    String h = req.getHeader("X-Forwarded-For");
    if (h != null && !h.isBlank()) {
      int comma = h.indexOf(',');
      return comma > 0 ? h.substring(0, comma).trim() : h.trim();
    }
    h = req.getHeader("X-Real-IP");
    if (h != null && !h.isBlank()) return h.trim();
    return req.getRemoteAddr();
  }

  private static String safeUA(HttpServletRequest req) {
    String ua = req.getHeader("User-Agent");
    if (ua == null) return "-";
    // keep log lines tidy; also avoid logging huge/binary UA
    ua = ua.replaceAll("\\s+", " ");
    return ua.length() > 200 ? ua.substring(0, 200) + "…" : ua;
  }
}
