package ru.practicum.shareit.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.abstraction.EntityDto;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestDto extends EntityDto {
    @JsonProperty("id")
    private Long requestId;
    @JsonProperty("description")
    private String requestDescription;
    @JsonProperty("created")
    private LocalDateTime requestDate;
    @JsonProperty("items")
    private List<RequestDto.ItemDtoForRequest> itemsDtoForRequestList;
    private Long userIdHeader;

    @Builder
    @Getter
    @EqualsAndHashCode(callSuper = false)
    @JsonInclude
    public static class ItemDtoForRequest {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private Long requestId;

        public static List<RequestDto.ItemDtoForRequest> createItemsDtoForRequestList(List<Item> itemsList) {
            List<RequestDto.ItemDtoForRequest> itemsDtoForRequestList = new ArrayList<>();

            for (Item item : itemsList) {
                itemsDtoForRequestList.add(ItemDtoForRequest.builder()
                        .id(item.getItemId())
                        .name(item.getItemName())
                        .description(item.getItemDescription())
                        .available(item.getIsItemAvailable())
                        .requestId(item.getRequest().getRequestId())
                        .build());
            }
            return itemsDtoForRequestList;
        }
    }
}
