package com.example.exodia.video.service;

import com.example.exodia.common.dto.*;
import com.example.exodia.common.dto.JanusCandidateRequest;
import com.example.exodia.common.dto.JanusSDPRequest;
import com.example.exodia.video.dto.JanusRequest;
import com.example.exodia.video.dto.JanusResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class JanusService {

    private static final String JANUS_URL = "http://janus-server:8088/janus";

    public String createSession() {
        RestTemplate restTemplate = new RestTemplate();
        String url = JANUS_URL + "/create";
        JanusResponse response = restTemplate.postForObject(url, null, JanusResponse.class);
        return response.getData().toString();
    }

    public String attachPlugin(String sessionId, String plugin) {
        RestTemplate restTemplate = new RestTemplate();
        String url = JANUS_URL + "/" + sessionId + "/attach";
        JanusRequest request = new JanusRequest("attach", plugin);
        JanusResponse response = restTemplate.postForObject(url, request, JanusResponse.class);
        return response.getData().toString();
    }

    public void sendSDP(String sessionId, String handleId, String sdp) {
        RestTemplate restTemplate = new RestTemplate();
        String url = JANUS_URL + "/" + sessionId + "/" + handleId + "/message";
        JanusSDPRequest request = new JanusSDPRequest(sdp);
        restTemplate.postForObject(url, request, JanusResponse.class);
    }

    public void sendCandidate(String sessionId, String handleId, String candidate) {
        RestTemplate restTemplate = new RestTemplate();
        String url = JANUS_URL + "/" + sessionId + "/" + handleId + "/trickle";
        JanusCandidateRequest request = new JanusCandidateRequest(candidate);
        restTemplate.postForObject(url, request, JanusResponse.class);

    }
}
