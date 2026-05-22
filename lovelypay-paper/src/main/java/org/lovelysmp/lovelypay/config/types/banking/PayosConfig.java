package org.lovelysmp.lovelypay.config.types.banking;

import de.exlll.configlib.Configuration;
import org.lovelysmp.lovelypay.config.annotations.Folder;

@Configuration
@Folder("banking/payos")
public class PayosConfig {
    public String clientId = "your-client-id";
    public String apiKey = "your-api-key";
    public String checksumKey = "your-checksum-key";
}
