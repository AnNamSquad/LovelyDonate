package org.simpmc.simppay.config.types;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;

@Configuration
public class MainConfig {
    public boolean debug = false;
    @Comment("Thời gian gọi API kiểm tra thẻ và giao dịch ngân hàng, tính theo giây")
    public int intervalApiCall = 5;
}
