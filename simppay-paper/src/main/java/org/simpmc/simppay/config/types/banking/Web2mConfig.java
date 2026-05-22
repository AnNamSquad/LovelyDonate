package org.simpmc.simppay.config.types.banking;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import org.simpmc.simppay.config.annotations.Folder;
import org.simpmc.simppay.data.bank.web2m.BankType;

@Configuration
@Folder("banking/web2m")
public class Web2mConfig {

    @Comment("Danh sách ngân hàng hỗ trợ: VCB, BIDV, BIDV_OPENAPI, MBBANK_OPENAPI, MBBBANK_LSGD, MBBANK_NOTI, ACB, TECHCOMBANK, TPBANK")
    public BankType bankType = BankType.ACB;

    @Comment("Tên đăng nhập banking")
    public String login = "stk";
    @Comment("Mật khẩu đăng nhập banking")
    public String password = "pass";
    @Comment("Mã token đăng nhập, lấy tại web2m")
    public String token = "123";

    @Comment("Số tài khoản ngân hàng")
    public String accountNumber = "123123123";

}
