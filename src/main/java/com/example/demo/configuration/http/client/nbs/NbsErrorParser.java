package com.example.demo.configuration.http.client.nbs;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import pro.cproject.lkpassengerbackend.admin.exception.AccessDeniedException;
import pro.cproject.lkpassengerbackend.admin.exception.ServerException;
import pro.cproject.lkpassengerbackend.admin.exception.UnauthorizedException;

import java.util.Iterator;
import java.util.Optional;

public class NbsErrorParser {
	private static final String ERRORS = "errors";
	private static final String DETAIL = "detail";
	private static final String TITLE = "title";

	public Exception parse(int status, String errorBody) {
		if (!HttpStatus.valueOf(status).isError()) return null;

		if (status == HttpStatus.UNAUTHORIZED.value()) {
			return new UnauthorizedException(errorBody);
		}
		if (status == HttpStatus.FORBIDDEN.value()) {
			return new AccessDeniedException(errorBody);
		}

		Optional<String> resultMessage = Optional.empty();

		JSONObject jsonError = JSONUtil.get(errorBody);
		if (!jsonError.isEmpty()) {
			// тут ошибки вида {"title":"Объект не найден","status":404,"detail":"Процесс переноса баланса/билетов транспортных карт с блокировкой не найден для пользователя: 27 и карты: 211 в состоянии WaitingTransfer."}
			// сначала смотрим в detail
			resultMessage = getErrorDetailFromDetailJsonKey(jsonError);

			if (resultMessage.isEmpty()) {
				// если ошибка такого типа {"errors":{"Login":["Номер телефона уже зарегистрирован."]},"title":"Ошибка валидации","status":400}
				// сначала смотрим в errors
				resultMessage = getErrorDetailFromErrorsJsonKey(jsonError);
			}

			if (resultMessage.isEmpty()) {
				// если не смогли найти нужные обьекты, то в конце проверяем title
				resultMessage = getErrorDetailFromTitleJsonKey(jsonError);
			}
		}

		// ошибку не смогла распарсить, то возьмем оригинал
		if (resultMessage.isEmpty()) {
			resultMessage = Optional.of(errorBody);
		}
		return new ServerException(resultMessage.get(), status);
	}

	private Optional<String> getErrorDetailFromTitleJsonKey(JSONObject error) {
		String errorByTitleKey = error.optString(TITLE);
		return !errorByTitleKey.isEmpty() ? Optional.of(errorByTitleKey) : Optional.empty();
	}

	private Optional<String> getErrorDetailFromDetailJsonKey(JSONObject error) {
		String errorByDetailKey = error.optString(DETAIL);
		return !errorByDetailKey.isEmpty() ? Optional.of(errorByDetailKey) : Optional.empty();
	}

	private Optional<String> getErrorDetailFromErrorsJsonKey(JSONObject error) {
		String errorsByErrorsKey = error.optString(ERRORS);
		StringBuilder concatenatedError = new StringBuilder();
		if (!errorsByErrorsKey.isEmpty()) {
			JSONObject errorsByErrorsKeyJson = JSONUtil.get(errorsByErrorsKey);
			Iterator<String> keys = errorsByErrorsKeyJson.keys();
			while (keys.hasNext()) {
				String key = keys.next();
				if (errorsByErrorsKeyJson.get(key) instanceof JSONArray) {
					JSONArray detailErrors = errorsByErrorsKeyJson.getJSONArray(key);
					for (Object detailError : detailErrors) {
						concatenatedError.append(detailError).append(" ");
					}
				}
			}
		}
		return !errorsByErrorsKey.isEmpty() ? Optional.of(concatenatedError.toString().trim()) : Optional.empty();
	}
}
