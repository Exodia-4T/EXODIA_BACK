package com.example.exodia.document.controller;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.exodia.common.dto.CommonErrorDto;
import com.example.exodia.common.dto.CommonResDto;
import com.example.exodia.document.domain.DocumentC;
import com.example.exodia.document.dto.DocDetailResDto;
import com.example.exodia.document.dto.DocListResDto;
import com.example.exodia.document.dto.DocReqDto;
import com.example.exodia.document.dto.DocRevertReqDto;
import com.example.exodia.document.dto.DocUpdateReqDto;
import com.example.exodia.document.service.DocumentService;
import com.example.exodia.user.dto.UserDeleteDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/document")
public class DocumentController {

	private final DocumentService documentService;

	@Autowired
	public DocumentController(DocumentService documentService) {
		this.documentService = documentService;
	}

	// 	문서 업로드
	@PostMapping("/uploadFile")
	public ResponseEntity<?> uploadDocument(@RequestPart(value = "file", required = false) MultipartFile file, @RequestPart(value = "data") DocReqDto docReqDto) {
		try {
			documentService.saveDoc(file, docReqDto);
			return ResponseEntity.ok("파일 저장 성공");
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("파일 저장 실패: " + e.getMessage());
		}
	}

	//	첨부파일 다운로드
	@GetMapping("/downloadFile/{id}")
	public ResponseEntity<?> downloadDocument(@PathVariable Long id, HttpServletResponse response) throws IOException {
		return documentService.downloadFile(id, response);
	}

	// 	전체 문서 조회
	@GetMapping("/list/all")
	public ResponseEntity<?> docList() {
		List<DocListResDto> docListResDtos = documentService.getDocList();
		return ResponseEntity.ok(new CommonResDto(HttpStatus.OK, "전체 문서 조회 성공", docListResDtos));
	}

	// 	최근 조회 문서 조회
	@GetMapping("/list/viewd")
	public ResponseEntity<?> docListByViewdAt() {
		List<DocListResDto> docListResDtos = documentService.getDocListByViewdAt();
		return ResponseEntity.ok(new CommonResDto(HttpStatus.OK, "최근 조회 문서 조회 성공", docListResDtos));
	}

	// 	최근 업데이트 문서 조회
	@GetMapping("/list/updated")
	public ResponseEntity<?> docListByUpdatedAt() {
		List<DocListResDto> docListResDtos = documentService.getDocListByUpdatedAt();
		return ResponseEntity.ok(new CommonResDto(HttpStatus.OK, "최근 업데이트 문서 조회 성공", docListResDtos));

	}

	// 	문서 상세조회
	@GetMapping("/detail/{id}")
	public ResponseEntity<?> detailDocument(@PathVariable Long id) {
		DocDetailResDto docDetailResDto = documentService.getDocDetail(id);
		return ResponseEntity.ok(new CommonResDto(HttpStatus.OK, "파일 정보 조회 성공", docDetailResDto));
	}

	// 	문서 업데이트
	@PostMapping("/update/{id}")
	public ResponseEntity<?> updateDocument(@RequestPart(value = "file", required = false) MultipartFile file,
		@RequestPart(value = "data") DocUpdateReqDto docUpdateReqDto) {
		try {
			DocumentC updatedDoc = documentService.updateDoc(file, docUpdateReqDto);
			return ResponseEntity.ok(new CommonResDto(HttpStatus.OK, "파일 업데이트 성공", updatedDoc));
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(new CommonErrorDto(HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}


	//  문서 버전 되돌리기
	// @PostMapping("/revert")
	// public ResponseEntity<DocDetailResDto> revertToVersion(DocRevertReqDto docRevertReqDto) {
	// 	DocDetailResDto response = documentService.revertToVersion(docRevertReqDto);
	// 	return ResponseEntity.ok(response);
	// }


	// 	문서 히스토리 조회
	@GetMapping("/{id}/versions")
	public ResponseEntity<?> getDocumentVersions(@PathVariable Long id){
		List<DocumentC> documentVersions = documentService.getDocumentVersions(id);
		return ResponseEntity.ok(new CommonResDto(HttpStatus.OK, "문서 히스토리 조회 성공", documentVersions));
	}

	// 	문서 삭제
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteDocument(@PathVariable Long id){
		try {
			documentService.deleteDocument(id);
			return ResponseEntity.ok(new CommonResDto(HttpStatus.OK, "문서 삭제 성공", null));
		} catch (RuntimeException e) {
			e.printStackTrace();
			return new ResponseEntity<>(new CommonErrorDto(HttpStatus.NOT_FOUND, e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}
}
