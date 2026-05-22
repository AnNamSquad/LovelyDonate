package org.simpmc.simppay.config.types;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Data;
import org.simpmc.simppay.api.DatabaseSettings;

@Data
@Configuration
public class DatabaseConfig implements DatabaseSettings {
    @Comment("Loại cơ sở dữ liệu để sử dụng. Các loại được hỗ trợ: MYSQL, H2")
    public String type = "H2";
    @Comment({"Dưới đây chỉ dành cho kết nối SQL", "Địa chỉ host của cơ sở dữ liệu MySQL"})
    public String host = "localhost";
    @Comment("Cổng của cơ sở dữ liệu MySQL")
    public int port = 3306;
    @Comment("Tên của cơ sở dữ liệu MySQL")
    public String database = "simppay";
    @Comment("Tên người dùng của cơ sở dữ liệu MySQL")
    public String username = "root";
    @Comment("Mật khẩu của cơ sở dữ liệu MySQL")
    public String password = "password";
}
