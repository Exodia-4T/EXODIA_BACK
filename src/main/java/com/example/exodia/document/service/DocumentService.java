package com.example.exodia.document.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.exodia.common.domain.DelYN;
import com.example.exodia.document.domain.DocumentC;
import com.example.exodia.document.domain.DocumentP;
import com.example.exodia.document.domain.DocumentType;
import com.example.exodia.document.dto.DocDetailResDto;
import com.example.exodia.document.dto.DocListResDto;
import com.example.exodia.document.dto.DocReqDto;
import com.example.exodia.document.dto.DocRevertReqDto;
import com.example.exodia.document.dto.DocUpdateReqDto;
import com.example.exodia.document.repository.DocumentCRepository;
import com.example.exodia.document.repository.DocumentPRepository;
import com.example.exodia.document.repository.DocumentTypeRepository;

@Service
@Transactional
public class DocumentService {

	private final DocumentCRepository documentCRepository;
	private final DocumentPRepository documentPRepository;
	private final DocumentTypeRepository documentTypeRepository;

	@Autowired
	public DocumentService(DocumentCRepository documentCRepository, DocumentPRepository documentPRepository,
		DocumentTypeRepository documentTypeRepository) {
		this.documentCRepository = documentCRepository;
		this.documentPRepository = documentPRepository;
		this.documentTypeRepository = documentTypeRepository;
	}

	// 	문서 업로드
	private String uploadDir = "/Users/suhyun/Documents/pot_doc";


	// 파일 저장
	public DocumentC saveDoc(MultipartFile file, DocReqDto docReqDto) throws IOException {
		String fileName = file.getOriginalFilename();
		Path filePath = Paths.get(uploadDir + "/" + fileName);
		Files.write(filePath, file.getBytes());

		String path = filePath.toString();
		String fileContent = new String(file.getBytes(), StandardCharsets.UTF_8);

		DocumentType documentType = documentTypeRepository.findByTypeName(docReqDto.getTypeName())
			.orElseGet(() -> {
				return documentTypeRepository.save(
					DocumentType.builder()
					.typeName(docReqDto.getTypeName()).delYn(DelYN.N).build());
			});
		DocumentC savedC = documentCRepository.save(DocumentC.toEntity(docReqDto, path, fileContent, documentType));
		savedC.updateDocumentP(saveParentDoc(savedC.getId(), documentType));
		return savedC;
	}

	public DocumentP saveParentDoc(Long id, DocumentType documentType) {
		return documentPRepository.save(DocumentP.toEntity(id, documentType));
	}

	//	첨부파일 다운로드
	public ResponseEntity<Resource> downloadFile(Long id, HttpServletResponse response) throws IOException {
		DocumentC doc = documentCRepository.findById(id).orElseThrow(() -> new NoSuchFileException("파일이 존재하지 않습니다"));
		Resource resource = new UrlResource(Paths.get(doc.getFilePath()).toUri());

		if (resource.exists() && resource.isReadable()) {
			return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
				.body(resource);
		} else {
			throw new IOException("파일을 읽어올 수 없음: " + doc.getFileName());
		}
	}

	// 	전체 문서 조회
	public List<DocListResDto> getDocList() {
		// 생성시간 == 수정시간 doc만 조회 -> 수정되지 않은 모든 데이터
		List<DocumentC> docs = documentCRepository.findAll();
		List<DocListResDto> docListResDtos = new ArrayList<>();
		for (DocumentC doc : docs) {
			docListResDtos.add(doc.fromEntityList());
		}
		return docListResDtos;
	}

	// 최근 열람 문서 조회
	public List<DocListResDto> getDocListByViewdAt() {
		List<DocumentC> docs = documentCRepository.findByOrderByViewedAtDesc();
		List<DocListResDto> docListResDtos = new ArrayList<>();
		for (DocumentC doc : docs) {
			docListResDtos.add(doc.fromEntityList());
		}
		return docListResDtos;
	}

	// 최근 수정 문서 조회
	public List<DocListResDto> getDocListByUpdatedAt() {
		List<DocumentC> docs = documentCRepository.findByOrderByUpdatedAtDesc();
		List<DocListResDto> docListResDtos = new ArrayList<>();
		for (DocumentC doc : docs) {
			docListResDtos.add(doc.fromEntityList());
		}
		return docListResDtos;
	}

	// 	문서 상세조회
	public DocDetailResDto getDocDetail(Long id) {
		DocumentC documentC = documentCRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("문서가 존재하지 않습니다."));
		documentC.updateViewdAt();    // 조회 시간 업데이트
		return documentC.fromEntity();
	}

	// 	문서 업데이트
	public DocumentC updateDoc(MultipartFile file, DocUpdateReqDto docUpdateReqDto) throws IOException {
		// 현재 문서
		DocumentC documentC = documentCRepository.findById(docUpdateReqDto.getId())
			.orElseThrow(() -> new EntityNotFoundException("문서가 존재하지 않습니다."));
		documentC.updateUpdatedAt();    // 수정 시간 업데이트
		DocumentP documentP = documentC.getDocumentP();

		// 새로운 파일로 수정
		String fileName = file.getOriginalFilename();
		Path filePath = Paths.get(uploadDir + "/" + fileName);
		Files.write(filePath, file.getBytes());

		String path = filePath.toString();
		String fileContent = new String(file.getBytes(), StandardCharsets.UTF_8);

		DocumentType documentType = documentTypeRepository.findByTypeName(docUpdateReqDto.getTypeName())
			.orElseThrow(() -> new EntityNotFoundException("폴더가 존재하지 않습니다."));

		// 자식 문서 추가
		DocumentC savedC = documentCRepository.save(DocumentC.updatetoEntity(docUpdateReqDto, path, fileContent, documentType));
		// 부모 문서 추가
		DocumentP updateP = documentP.updateEntity(docUpdateReqDto.getId(), documentP.getVersion());
		// 자식 문서 업데이트
		savedC.updateDocumentP(updateP);

		documentCRepository.save(savedC);
		documentPRepository.save(updateP);
		return savedC;
	}


	// 문서 버전
	//  public DocDetailResDto revertToVersion(DocRevertReqDto docRevertReqDto) {
	// 	DocumentC currentDoc = documentCRepository.findById(docRevertReqDto.getId())
	// 		.orElseThrow(() -> new EntityNotFoundException("문서가 존재하지 않습니다."));
	//
	// 	currentDoc.updateContents(docRevertReqDto.version);
	// 	currentDoc.updateUpdatedAt();
	// 	documentCRepository.save(currentDoc);
	//
	// 	return currentDoc.fromEntity();
	// }

	// 	문서 히스토리 조회
	public List<DocumentC> getDocumentVersions(Long id) {
		DocumentC documentC = documentCRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("문서가 존재하지 않습니다."));

		List<DocumentC> versions = new ArrayList<>();
		versions.add(documentC);
		// 모든 버전(부모 문서들) 조회
		if (documentC != null && documentC.getDocumentP() != null) {
			DocumentP documentP = documentC.getDocumentP();
			documentC = documentCRepository.findById(documentP.getId())
				.orElseThrow(() -> new EntityNotFoundException("히스토리가 존재하지 않습니다."));
			versions.add(documentC);
		}
		return versions;
	}

	// 문서 삭제 -> 해당 문서만 삭제
	public void deleteDocument(Long id) {
		DocumentC documentC = documentCRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("문서가 존재하지 않습니다."));
		documentC.softDelete();
		documentCRepository.save(documentC);
	}
}
