package com.example.exodia.calendar.service;

import com.example.exodia.calendar.dto.CalendarSaveDto;
import com.example.exodia.calendar.dto.CalendarUpdateDto;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {

    // 클라이언트 명
    private static final String APPLICATION_NAME = "Google Calendar API with Service Account";
    // 요청 + 응답 JSON 처리 팩토리
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    // 자격 증명 파일 경로
    @Value("${google.credentials.file.path}")
    private String credentialsFilePath;

    public Calendar getCalendarService() throws GeneralSecurityException, IOException {
        GoogleCredential credential;

        // 서비스 계정 JSON 파일 경로로부터 GoogleCredential 객체 생성
        try (InputStream credentialsStream = new ClassPathResource(credentialsFilePath).getInputStream()) {
            credential = GoogleCredential.fromStream(credentialsStream)
                    // 접근 범위 인증객체 설정
                    .createScoped(Collections.singleton(CalendarScopes.CALENDAR));
        }
        return new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    // Google API 에서는 LOCALDATETIME 을 지원하지 않기 때문에 LOCALDATETIME ->zone을 통한 밀리초 변환 -> datetime
    private DateTime convertToGoogleDateTime(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Seoul"));
        return new DateTime(zonedDateTime.toInstant().toEpochMilli());
    }

    /*  구글 캘린더에 이벤트 추가 */
    public Event addEventToGoogleCalendar(CalendarSaveDto dto) throws GeneralSecurityException, IOException {
        Calendar service = getCalendarService();

        Event event = new Event()
                .setSummary(dto.getTitle())
                .setDescription(dto.getContent());

        DateTime startDateTime = convertToGoogleDateTime(LocalDateTime.parse(dto.getStartTime()));
        EventDateTime start = new EventDateTime().setDateTime(startDateTime).setTimeZone("Asia/Seoul");
        event.setStart(start);

        DateTime endDateTime = convertToGoogleDateTime(LocalDateTime.parse(dto.getEndTime()));
        EventDateTime end = new EventDateTime().setDateTime(endDateTime).setTimeZone("Asia/Seoul");
        event.setEnd(end);

        return service.events().insert("exodia9800@gmail.com", event).execute();
    }
    /*  구글 캘린더에 이벤트 수정 */
    public Event updateEventInGoogleCalendar(String googleEventId, CalendarUpdateDto dto) throws GeneralSecurityException, IOException {
        Calendar service = getCalendarService();

        // Google Calendar에서 기존 이벤트 가져오기
        Event event = service.events().get("exodia9800@gmail.com", googleEventId).execute();

        // 업데이트할 정보 설정
        event.setSummary(dto.getTitle());
        event.setDescription(dto.getContent());

        DateTime startDateTime = convertToGoogleDateTime(LocalDateTime.parse(dto.getStartTime()));
        EventDateTime start = new EventDateTime().setDateTime(startDateTime).setTimeZone("Asia/Seoul");
        event.setStart(start);

        DateTime endDateTime = convertToGoogleDateTime(LocalDateTime.parse(dto.getEndTime()));
        EventDateTime end = new EventDateTime().setDateTime(endDateTime).setTimeZone("Asia/Seoul");
        event.setEnd(end);

        // Google Calendar에 업데이트된 이벤트 반영
        return service.events().update("exodia9800@gmail.com", googleEventId, event).execute();
    }

    /*  구글 캘린더에 이벤트 삭제 */
    public void deleteEventInGoogleCalendar(String googleEventId) throws GeneralSecurityException, IOException {
        Calendar service = getCalendarService();
        service.events().delete("exodia9800@gmail.com", googleEventId).execute();
    }

    public List<Event> getHolidayEvents(String calendarId, LocalDateTime startDate, LocalDateTime endDate) throws GeneralSecurityException, IOException {
        Calendar service = getCalendarService();

        DateTime timeMin = convertToGoogleDateTime(startDate);
        DateTime timeMax = convertToGoogleDateTime(endDate);

        Events events = service.events().list(calendarId)
                .setTimeMin(timeMin)
                .setTimeMax(timeMax)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return events.getItems();
    }

}
/* CalendarService 인스턴스를 생성 */
//    private Calendar getCalendarService() throws Exception {
//        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//        Credential credential = getCredentials(HTTP_TRANSPORT);
//
//        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//    }
/* OAuth 2.0 인증 */
//    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws Exception {
//        InputStream in = CalendarService.class.getResourceAsStream(credentialsFilePath);
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, Collections.singleton(CalendarScopes.CALENDAR))
//                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(tokensDirectoryPath)))
//                .setAccessType("offline")
//                .build();
//
//        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
//        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
//    }
//
//    /* 이벤트 생성(일정 생성) */
//    public String createGoogleCalendarEvent(CalendarSaveDto dto) throws Exception {
//        Calendar service = getCalendarService();
//
//        // 이벤트 생성
//        Event event = new Event()
//                .setSummary(dto.getTitle())
//                .setDescription(dto.getContent());
//
//        // 시작시간
//        DateTime startDateTime = new DateTime(dto.getStartTime().toString() + ":00Z");
//        EventDateTime start = new EventDateTime()
//                .setDateTime(startDateTime)
//                .setTimeZone("Asia/Seoul");
//        event.setStart(start);
//
//        // 종료시간
//        DateTime endDateTime = new DateTime(dto.getEndTime().toString() + ":00Z");
//        EventDateTime end = new EventDateTime()
//                .setDateTime(endDateTime)
//                .setTimeZone("Asia/Seoul");
//        event.setEnd(end);
//
//        // 이벤트 삽입
//        event = service.events().insert("primary", event).execute();
//        return event.getId();
//    }
//
//    /* 달 기준 이벤트 조회 */
//    public List<Event> getGoogleCalendarEventsByMonth(CalendarListDto dto) throws Exception {
//        com.google.api.services.calendar.Calendar service = getCalendarService();
//        String monthStart = String.format("%04d-%02d-01T00:00:00Z", dto.getYear(), dto.getMonth());
//        String monthEnd = String.format("%04d-%02d-%02dT23:59:59Z", dto.getYear(), dto.getMonth(), getLastDayOfMonth(dto.getYear(), dto.getMonth()));
//
//        DateTime timeMin = new DateTime(monthStart);
//        DateTime timeMax = new DateTime(monthEnd);
//
//        Events events = service.events().list("primary")
//                .setTimeMin(timeMin)
//                .setTimeMax(timeMax)
//                .setOrderBy("startTime")
//                .setSingleEvents(true)
//                .execute();
//
//        return events.getItems();
//    }
//
//    private int getLastDayOfMonth(int year, int month) {
//        java.util.Calendar calendar = java.util.Calendar.getInstance();
//        calendar.set(year, month - 1, 1);
//        return calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
//    }
//
//
//
//    /*  이벤트 수정 */
//    public String updateGoogleCalendarEvent(String eventId, CalendarSaveDto dto) throws Exception {
//        Calendar service = getCalendarService();
//
//        // 기존 이벤트 조회
//        Event event = service.events().get("primary", eventId).execute();
//
//        // 이벤트 정보 업데이트
//        event.setSummary(dto.getTitle());
//        event.setDescription(dto.getContent());
//
//        DateTime startDateTime = new DateTime(dto.getStartTime().toString() + ":00Z");
//        EventDateTime start = new EventDateTime()
//                .setDateTime(startDateTime)
//                .setTimeZone("Asia/Seoul");
//        event.setStart(start);
//
//        DateTime endDateTime = new DateTime(dto.getEndTime().toString() + ":00Z");
//        EventDateTime end = new EventDateTime()
//                .setDateTime(endDateTime)
//                .setTimeZone("Asia/Seoul");
//        event.setEnd(end);
//
//        event = service.events().update("primary", eventId, event).execute();
//        return event.getId();
//    }
//
//    /*  이벤트 삭제(일정삭제) */
//    public void deleteGoogleCalendarEvent(String eventId) throws Exception {
//        Calendar service = getCalendarService();
//        service.events().delete("primary", eventId).execute();
//    }
//}
