package lk.ijse.SpringProject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID uuid;
    private String email;
    private String password;
    private String name;
    private  String companyName;
    private  String role;
}
