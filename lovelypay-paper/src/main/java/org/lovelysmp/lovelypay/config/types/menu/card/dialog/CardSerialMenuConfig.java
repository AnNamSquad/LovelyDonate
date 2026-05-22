package org.lovelysmp.lovelypay.config.types.menu.card.dialog;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import org.bukkit.Material;
import org.lovelysmp.lovelypay.config.annotations.Folder;
import org.lovelysmp.lovelypay.config.types.data.menu.DisplayItem;

import java.util.List;

@Configuration
@Folder("menus")
public class CardSerialMenuConfig {
    @Comment("Title có hỗ trợ PlaceholderAPI")
    public String title = "<gradient:#E34949:#D8DB5C><bold>LovelyPay</bold><white> Nhập Số Serial";

    public DisplayItem item = DisplayItem.builder()
            .material(Material.DIAMOND)
            .amount(1)
            .name("<green>Nhập Số Serial...")
            .lores(List.of(
                    "<color:#24d65d> Nhập số serial của thẻ cào!"
            ))
            .build();
}
