package com.isa.cottages.DTO;

import com.isa.cottages.FieldMatch.FieldMatch;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldMatch(first = "newPassword", second = "confirmNewPassword", message = "The password fields must match!")
public class ChangePasswordDTO {

    private String newPassword;

    private String confirmNewPassword;
}
