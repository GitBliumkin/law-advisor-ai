package com.shared.mvc.advice;

import com.shared.basecrud.dtos.responses.BaseListResponse;
import com.shared.basecrud.dtos.responses.BaseResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

@RestControllerAdvice
public class ResponseEnvelopeAdvice implements ResponseBodyAdvice<Object> {

  @Override
  public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> c) {
    Class<?> rt = returnType.getParameterType();
    return BaseResponse.class.isAssignableFrom(rt) || BaseListResponse.class.isAssignableFrom(rt);
  }

  @Override
  public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType mediaType,
                                Class<? extends HttpMessageConverter<?>> converterType,
                                ServerHttpRequest request, ServerHttpResponse response) {
    if (body instanceof BaseResponse<?> br && br.getError() == null) {
      HttpMethod m = request.getMethod();
      if (m == HttpMethod.POST) {
        response.setStatusCode(HttpStatus.CREATED);
      } else if (m == HttpMethod.DELETE) {
        response.setStatusCode(br.getPayload() == null ? HttpStatus.NO_CONTENT : HttpStatus.OK);
      } else {
        response.setStatusCode(HttpStatus.OK);
      }
    } else if (body instanceof BaseListResponse<?>) {
      response.setStatusCode(HttpStatus.OK);
    }
    return body;
  }
}
