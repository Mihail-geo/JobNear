package pro.cproject.lkpassengerbackend.admin.service;

import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
	String save(MultipartFile file);

	GridFsResource getFileResource(String fileName);

	void delete(String fileName);
}
