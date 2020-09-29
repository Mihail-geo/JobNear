package pro.cproject.lkpassengerbackend.admin.service.impl;

import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pro.cproject.lkpassengerbackend.admin.exception.ServerException;
import pro.cproject.lkpassengerbackend.admin.service.FileStorageService;
import pro.cproject.lkpassengerbackend.admin.util.FileUtil;
import pro.cproject.lkpassengerbackend.admin.util.Validator;

import java.io.IOException;

/**
 * @author vladi_geras on 07.06.2019
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {
	private final GridFsTemplate gridFsTemplate;

	@Override
	public String save(MultipartFile file) {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename()).toLowerCase();
		try {
			if (!FileUtil.isValidFileName(fileName)) {
				throw new ServerException("Фаил " + fileName + " содержит недопустимые символы");
			}

			int i = 1;
			String fileNameTemp = fileName;
			while (getFileIfExist(fileName) != null) {
				fileName = i + "_" + fileNameTemp;
				i++;
			}

			gridFsTemplate.store(file.getInputStream(), fileName, file.getContentType());

			log.info("Был добавлен новый фаил в хранилище " + fileName);
			return fileName;
		} catch (IOException ex) {
			throw new ServerException("Не удалось сохранить фаил " + fileName);
		}
	}

	@Override
	public GridFsResource getFileResource(String fileName) {
		if (Validator.isEmpty(fileName)) throw new ServerException("Некорректное значение параметра 'fileName'");

		GridFsResource resource = null;
		GridFSFile file = getFileIfExist(fileName);
		if (file != null) {
			resource = gridFsTemplate.getResource(file);
		}
		return resource;
	}

	@Override
	public void delete(String fileName) {
		if (!Validator.isEmpty(fileName)) {
			gridFsTemplate.delete(new Query(GridFsCriteria.whereFilename().is(fileName)));
		}
	}

	private GridFSFile getFileIfExist(String filename) {
		if (filename == null) return null;

		return gridFsTemplate
				.find(new Query((GridFsCriteria.whereFilename().is(filename))))
				.first();
	}
}
