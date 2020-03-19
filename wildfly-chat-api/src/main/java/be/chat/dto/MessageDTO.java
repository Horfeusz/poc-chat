package be.chat.dto;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class MessageDTO implements Serializable {

    private String owner;

    private Date time;

    private String message;
}
