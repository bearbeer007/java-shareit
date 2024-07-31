package ru.practicum.shareit.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends PagingAndSortingRepository<ItemRequest, Long>, JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestorId(Long requestorId);

    Optional<ItemRequest> findByIdOrderByCreatedAsc(Long itemRequestId);
}
