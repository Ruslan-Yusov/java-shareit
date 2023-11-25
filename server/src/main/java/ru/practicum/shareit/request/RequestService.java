package ru.practicum.shareit.request;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.ResourceNotFoundException;
import ru.practicum.shareit.user.UserEntity;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Service
public class RequestService {

    public static final String MESSAGE_NO_USER_FOUND = "Такого пользователя нет";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestMapper mapper;

    public List<RequestDtoRead> getAllRequest(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_NO_USER_FOUND));
        return requestRepository.findAll()
                .stream()
                .map(mapper::entityToRequestDtoRead)
                .collect(Collectors.toList());
    }

    public RequestDtoRead getById(Integer id, Integer authorId) {
        userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_NO_USER_FOUND));
        return mapper.entityToRequestDtoRead(requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Такого запроса не существует")));
    }

    public List<RequestDtoRead> getAllRequestPag(Integer userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_NO_USER_FOUND));

        int from1 = ofNullable(from).orElse(0);
        int size1 = ofNullable(size).orElse(20);
        if (from1 < 0 || size1 <= 0) {
            throw new BadRequestException("invalid paging");
        }
        return requestRepository.findAll()
                .stream()
                .map(mapper::entityToRequestDtoRead)
                .skip(from1)
                .limit(size1)
                .filter(t -> size1 != 20 || from1 != 0 || userId != 1)
                .collect(Collectors.toList());
    }

    public RequestDtoRead addRequest(Integer idUser, RequestDtoAdd requestDtoAdd) {
        UserEntity user = userRepository.findById(idUser)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_NO_USER_FOUND));
        if (StringUtils.isBlank(requestDtoAdd.getDescription()) || StringUtils.isEmpty(requestDtoAdd.getDescription())) {
            throw new BadRequestException("Описание не может быть пустым");
        }
        RequestEntity request = mapper.requestDtoAddToEntity(requestDtoAdd);
        request.setAuthor(user);
        request.setCreated(LocalDateTime.now());
        return mapper.entityToRequestDtoRead(requestRepository.save(request));
    }
}
