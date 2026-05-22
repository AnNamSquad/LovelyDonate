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
public class CardListMenuConfig {

    @Comment("Title có hỗ trợ PlaceholderAPI")
    public String title = "<gradient:#E34949:#D8DB5C><bold>SimpPay</bold><white> Chọn Nhà Mạng";
    public List<String> layout = Arrays.asList(
            "#########",
            "#O#O#O#O#",
            "#O#O#O#O#",
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

    public DisplayItem cardItem = DisplayItem.builder()
            .material(Material.PAPER)
            .amount(1)
            .name("<white>Nhà Mạng:<color:#24d65d> <bold>{card_name}")
            .lores(List.of(
                    "<dark_green> ➜</dark_green><color:#00ff00> Click để chọn nhà mạng này!"
            ))
            .build();
}
