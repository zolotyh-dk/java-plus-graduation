package ru.practicum.ewm.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.exception.HttpRequestResponseLogger;
import ru.practicum.ewm.service.RequestService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/internal/requests")
@RequiredArgsConstructor
public class RequestInternalController extends HttpRequestResponseLogger {
    private final RequestService service;

    @PostMapping("/stats")
    public Map<Long, Long> getConfirmedRequestsStats(@RequestBody List<Long> eventIds, HttpServletRequest request) {
        logHttpRequest(request);
        Map<Long, Long> confirmedRequests = service.getConfirmedRequestsStats(eventIds);
        logHttpResponse(request, confirmedRequests);
        return confirmedRequests;
    }

    @GetMapping("/exist")
    public boolean existConfirmedParticipation(@RequestParam long userId, @RequestParam long eventId, HttpServletRequest request) {
        logHttpRequest(request, userId);
        boolean isExists = service.existsByRequesterIdAndEventId(userId, eventId);
        logHttpResponse(request, isExists);
        return isExists;
    }
}