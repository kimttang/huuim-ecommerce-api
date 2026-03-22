package com.huuim.ecommerce.global.exception;

/**
 * 왜:
 * - 모든 에러 응답 포맷을 통일하여 클라이언트 처리 단순화
 * - 상태코드 + 메시지만 최소한으로 전달 (과도한 정보 노출 방지)
 */
public record ErrorResponse(
        int status,
        String message
) {
}