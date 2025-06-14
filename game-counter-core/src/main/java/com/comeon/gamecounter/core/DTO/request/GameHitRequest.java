package com.comeon.gamecounter.core.DTO.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GameHitRequest {
    private final String sessionId;
    private final String gameCode;
}
