package be.chat.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class MessageDTO implements Serializable {

    private String owner;

    private LocalDateTime time;

    private String message;
}
