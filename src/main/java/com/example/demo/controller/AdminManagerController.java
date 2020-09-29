package com.example.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.demo.aop.Protected;
import com.example.demo.constant.SecurityConstant;
import com.example.demo.exception.BadParamException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.entity.ManagerEntity;
import com.example.demo.model.embedded.NameEmbedded;
import com.example.demo.model.enumeration.Module;
import com.example.demo.model.enumeration.ModuleScope;
import com.example.demo.service.ManagerService;
import com.example.demo.util.HttpUtil;
import com.example.demo.util.Validator;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Validated
@RequiredArgsConstructor
@Api(tags = {"Admin Manager"})
@RestController
@RequestMapping(SecurityConstant.ADMIN_PATH + "/managers")
public class AdminManagerController {
    private final ManagerService managerService;

    @Protected(module = Module.MANAGERS, scope = ModuleScope.WRITE)
    @ApiOperation(value = "Создание менеджера")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping({"/v1.0"})
    public DataResponse<LongId> save(@ApiParam(value = "менеджер")
                                     @RequestBody CreateUpdateManager manager) {

        if (manager == null || Validator.isEmpty(manager.getEmail()) || !Validator.isEmailValid(manager.getEmail()))
            throw new BadParamException("Некорректное значение параметра 'email'");

        NameEmbedded name = manager.getName();
        if (name == null) throw new BadParamException("Некорректное значение параметра 'name'");
        if (Validator.isEmpty(name.getFirstName())) throw new BadParamException("Не указано имя");
        if (Validator.isEmpty(name.getLastName())) throw new BadParamException("Не указана фамилия");
        if (!manager.isAdmin() && (manager.getAccess() == null || manager.getAccess().isEmpty())) {
            throw new BadParamException("Не указан(ы) доступ(ы)");
        }

        return new DataResponse<>(new LongId(managerService.save(manager, null).getId()));
    }

    @Protected(module = Module.MANAGERS, scope = ModuleScope.WRITE)
    @ApiOperation(value = "Обновление менеджера")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping({"/v1.0/{id}"})
    public SuccessResponse update(@ApiParam(value = "id менеджера")
                                  @PathVariable(value = "id") @Min(1) Long managerId,
                                  @ApiParam(value = "менеджер")
                                  @RequestBody CreateUpdateManager manager) {

        if (!Validator.isLongValid(managerId)) throw new BadParamException("Некорректное значение параметра 'id'");

        managerService.save(manager, managerId);
        return new SuccessResponse();
    }

    @ApiOperation(value = "Получение менеджеров")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping({"/v1.0"})
    public DataResponse<PageResponse<Manager>> get(@ApiParam(value = "номер страницы")
                                                   @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                   @ApiParam(value = "количество на странице")
                                                   @RequestParam(value = "size", required = false, defaultValue = "20") int size,
                                                   @ApiParam(value = "поиск по имени")
                                                   @RequestParam(value = "searchByName", required = false, defaultValue = "") String name) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        return new DataResponse<>(
                new PageResponse<>(managerService
                        .find(pageable, name)
                        .map(Manager::new)));
    }

    @ApiOperation(value = "Получение менеджера")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping({"/v1.0/{id}"})
    public DataResponse<Manager> get(@ApiParam(value = "id менеджера")
                                     @PathVariable(value = "id") @Min(1) Long managerId) {
        return new DataResponse<>(
                new Manager(
                        managerService
                                .find(managerId)
                                .orElseThrow(() -> new NotFoundException("Менеджер с id = " + managerId))));
    }

    @Protected(module = Module.MANAGERS, scope = ModuleScope.WRITE)
    @ApiOperation(value = "Блокировка / разблокировка менеджера")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping({"/v1.0/{id}/block"})
    public SuccessResponse block(@ApiParam(value = "id менеджера")
                                 @PathVariable(value = "id") @Min(1) Long managerId,
                                 @ApiParam(value = "заблокировать (true) / разблокировать (false)")
                                 @RequestParam(value = "lock", defaultValue = "false") boolean lock) {
        Long principalId = HttpUtil.getManagerIdFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
        if (managerId.equals(principalId)) throw new BadParamException("Нельзя заблокировать / разблокировать себя");

        managerService.block(managerId, lock);
        return new SuccessResponse();
    }

    @ApiOperation("Получение информации о текущем менеджере")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping({"/v1.0/info"})
    public DataResponse<Manager> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        ManagerEntity manager;
        if (principal instanceof CprojectManagerDetails) {
            final CprojectManagerDetails managerDetails = (CprojectManagerDetails) principal;
            manager = managerService
                    .find(managerDetails.getId())
                    .orElseThrow(() -> new NotFoundException("Менеджер с id = " + managerDetails.getId()));
        } else {
            String principalString = principal.toString();
            manager = managerService
                    .findByEmail(principalString)
                    .orElseThrow(() -> new NotFoundException("Менеджер с email = " + principal));
        }
        return new DataResponse<>(new Manager(manager));
    }

    @ApiOperation(value = "Изменение пароля")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping({"/v1.0/password"})
    public SuccessResponse changePassword(@ApiParam(value = "запрос изменения пароля")
                                          @RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        managerService.changePassword(changePasswordRequest);
        return new SuccessResponse();
    }
}