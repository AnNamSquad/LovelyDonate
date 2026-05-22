package org.simpmc.simppay.config.types.menu.card;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import org.bukkit.Material;
import org.simpmc.simppay.config.annotations.Folder;
import org.simpmc.simppay.config.types.data.menu.DisplayItem;
import org.simpmc.simppay.config.types.data.menu.RoleType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Configuration
@Folder("menus")
public class CardPriceMenuConfig {
    @Comment("Title có hỗ trợ PlaceholderAPI")
    public String title = "<gradient:#E34949:#D8DB5C><bold>SimpPay</bold><white> Chọn Mệnh Giá";
    public List<String> layout = Arrays.asList(
            "#########",
            "##OOOOO##",
            "##OO#OO##",
            "#########"
    );
    @Comment({"Bản đồ các mục theo ký tự", "'O' là hiển thị thẻ"})
    public Map<Character, DisplayItem> displayItems = Map.of(
            '#', DisplayItem.builder()
                    .name(" ")
                    .material(Material.GRAY_STAINED_GLASS_PANE)
                    .role(RoleType.NONE)
                    .amount(1)
                    .build()
    );

    public DisplayItem priceItem = DisplayItem.builder()
            .material(Material.BOOK)
            .amount(1)
            .name("<white>Mệnh Giá:<color:#24d65d> <bold>{price_name}")
            .lores(List.of(
                    "<dark_green> ➜</dark_green><color:#00ff00> Click để chọn mệnh giá này!"
            ))
            .build();


}
